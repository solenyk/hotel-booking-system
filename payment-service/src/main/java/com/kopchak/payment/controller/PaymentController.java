package com.kopchak.payment.controller;

import com.kopchak.payment.dto.StripeCredentialsDto;
import com.kopchak.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private static final String STRIPE_HEADER = "Stripe-Signature";

    //TEST CARD: 4242 4242 4242 4242
    @PostMapping("/{bookingId}")
    public ResponseEntity<String> stripeCheckout(@Valid @RequestBody StripeCredentialsDto credentialsDto,
                                               @PathVariable(name = "bookingId") Integer bookingId) throws StripeException {
        String stripeCheckoutUserUrl = paymentService.stripeCheckout(credentialsDto, bookingId);
        return ResponseEntity.ok(stripeCheckoutUserUrl);
//        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(stripeCheckoutUserUrl)).build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handlePaymentWebhook(HttpServletRequest request) throws IOException, StripeException {
        String sigHeader = request.getHeader(STRIPE_HEADER);
        String requestBody = IOUtils.toString(request.getReader());
        paymentService.handlePaymentWebhook(sigHeader, requestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}