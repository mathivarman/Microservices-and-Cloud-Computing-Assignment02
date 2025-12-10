package com.travel.payment.controller;

import com.travel.payment.dto.PaymentDTO;
import com.travel.payment.dto.PaymentRequestDTO;
import com.travel.payment.dto.PaymentResponseDTO;
import com.travel.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Service", description = "Payment processing APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process payment", description = "Processes payment and confirms booking via WebClient")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        log.info("POST /api/payments/process - Processing payment for booking {}", requestDTO.getBookingId());
        
        PaymentDTO paymentDTO = paymentService.processPayment(requestDTO);
        String message = paymentDTO.getStatus().equals("SUCCESS") 
                ? "Payment processed successfully" 
                : "Payment processing failed";
        
        PaymentResponseDTO response = new PaymentResponseDTO(
                paymentDTO.getStatus().equals("SUCCESS"),
                message,
                paymentDTO
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        log.info("GET /api/payments/{}", id);
        PaymentDTO paymentDTO = paymentService.getPaymentById(id);
        PaymentResponseDTO response = new PaymentResponseDTO(
                true, "Payment retrieved successfully", paymentDTO
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payment by booking ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentByBookingId(@PathVariable Long bookingId) {
        log.info("GET /api/payments/booking/{}", bookingId);
        PaymentDTO paymentDTO = paymentService.getPaymentByBookingId(bookingId);
        PaymentResponseDTO response = new PaymentResponseDTO(
                true, "Payment retrieved successfully", paymentDTO
        );
        return ResponseEntity.ok(response);
    }
}
