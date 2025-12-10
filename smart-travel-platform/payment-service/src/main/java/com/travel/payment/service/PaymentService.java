package com.travel.payment.service;

import com.travel.payment.dto.PaymentDTO;
import com.travel.payment.dto.PaymentRequestDTO;
import com.travel.payment.entity.Payment;
import com.travel.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Payment Service with WebClient integration to call Booking Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient bookingServiceWebClient;

    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO requestDTO) {
        log.info("Processing payment for booking ID: {}", requestDTO.getBookingId());
        
        Payment payment = new Payment();
        payment.setBookingId(requestDTO.getBookingId());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentMethod(requestDTO.getPaymentMethod());
        
        // Simulate payment processing (90% success rate)
        boolean paymentSuccess = Math.random() > 0.1;
        payment.setStatus(paymentSuccess ? "SUCCESS" : "FAILED");
        
        Payment savedPayment = paymentRepository.save(payment);
        
        if (paymentSuccess) {
            log.info("💳 Payment successful! Transaction ID: {}", savedPayment.getTransactionId());
            
            // Call Booking Service to confirm booking via WebClient
            try {
                confirmBookingViaWebClient(requestDTO.getBookingId());
            } catch (Exception e) {
                log.error("Failed to confirm booking via WebClient: {}", e.getMessage());
            }
        } else {
            log.error("❌ Payment failed for booking ID: {}", requestDTO.getBookingId());
        }
        
        return convertToDTO(savedPayment);
    }

    /**
     * Call Booking Service to confirm booking using WebClient
     */
    private void confirmBookingViaWebClient(Long bookingId) {
        log.info("Calling Booking Service via WebClient to confirm booking {}", bookingId);
        
        try {
            bookingServiceWebClient
                    .put()
                    .uri("/api/bookings/{id}/confirm", bookingId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("✅ Booking confirmed via WebClient: {}", response),
                            error -> log.error("❌ Error confirming booking: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("WebClient call failed: {}", e.getMessage());
        }
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        log.info("Fetching payment with id: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        return convertToDTO(payment);
    }

    public PaymentDTO getPaymentByBookingId(Long bookingId) {
        log.info("Fetching payment for booking id: {}", bookingId);
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking id: " + bookingId));
        return convertToDTO(payment);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate()
        );
    }
}
