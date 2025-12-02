package com.kopchak.payment.service;

import com.kopchak.payment.dto.StripeCredentialsDto;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    @Value(value = "${stripe.api.key}")
    private String STRIPE_API_KEY;

    @Value(value = "${stripe.success.url}")
    private String STRIPE_SUCCESS_URL;

    @Value(value = "${stripe.webhook.secret.key}")
    private String WEBHOOK_SECRET_KEY;

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_API_KEY;
    }

    private static final String CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";
    private static final String SUCCESSFUL_DELAYED_PAYMENT = "checkout.session.async_payment_succeeded";
    private static final String FAILED_DELAYED_PAYMENT = "checkout.session.async_payment_failed";
    private static final String PAID_SESSION_PAYMENT_STATUS = "paid";
    private static final String SESSION_BOOKING_ID_METADATA_KEY = "booking_id";

    public String stripeCheckout(StripeCredentialsDto credentialsDto, Integer bookingId)
            throws StripeException {
        //TODO: check if booking exists
        Customer customer = findOrCreateStripeCustomer(credentialsDto.customerEmail(), credentialsDto.customerName());
        //TODO: add price
        SessionCreateParams sessionCreateParams = createPaymentSessionParams(customer, bookingId);
        Session session = Session.create(sessionCreateParams);
        return session.getUrl();
    }

    public void handlePaymentWebhook(String sigHeader, String requestBody)
            throws StripeException {
        Event event = Webhook.constructEvent(requestBody, sigHeader, WEBHOOK_SECRET_KEY);

        String eventType = event.getType();
        if (eventType.equals(CHECKOUT_SESSION_COMPLETED) || eventType.equals(SUCCESSFUL_DELAYED_PAYMENT) ||
                eventType.equals(FAILED_DELAYED_PAYMENT)) {

            Session sessionEvent = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(() ->
                    new EventDataObjectDeserializationException("Event data object deserialization is impossible",
                            event.toJson()));
            SessionRetrieveParams params = SessionRetrieveParams.builder()
                    .addExpand("line_items")
                    .build();
            Session session = Session.retrieve(sessionEvent.getId(), params, null);
            Map<String, String> sessionMetadata = session.getMetadata();
            String bookingId = sessionMetadata.get(SESSION_BOOKING_ID_METADATA_KEY);

            System.out.println("PAYED!!!");

//            buildPayment(String orderId, String eventType, String paymentId, String sessionPaymentStatus,
//                              Long orderTotalAmount)
        }
    }

    private Customer findOrCreateStripeCustomer(String email, String name) throws StripeException {
        CustomerSearchParams params = CustomerSearchParams.builder().setQuery("email:'" + email + "'").build();
        CustomerSearchResult customerSearchResult = Customer.search(params);
        if (customerSearchResult.getData().isEmpty()) {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(email)
                    .build();
            return Customer.create(customerCreateParams);
        } else {
            return customerSearchResult.getData().get(0);
        }
    }

    private SessionCreateParams createPaymentSessionParams(Customer customer, Integer bookingId) {
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setCurrency("UAH")
                        .putMetadata(SESSION_BOOKING_ID_METADATA_KEY, bookingId.toString())
                        .setSuccessUrl(STRIPE_SUCCESS_URL);

        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Hotel room booking")
                                                        .build()
                                        )
                                        .setCurrency("UAH")
                                        .setUnitAmountDecimal(new BigDecimal(500 * 100))
                                        .build())
                        .setQuantity(1L)
                        .build());
        return paramsBuilder.build();
    }

//    private void buildPayment(String orderId, String eventType, String paymentId, String sessionPaymentStatus,
//                              Long orderTotalAmount) {
//        Order order = orderRepository.findById(orderId).orElseThrow();
//
//        PaymentStatus paymentStatus;
//        OrderStatus orderStatus;
//
//        if (eventType.equals(FAILED_DELAYED_PAYMENT)) {
//            paymentStatus = PaymentStatus.FAILED;
//            orderStatus = OrderStatus.CANCELED;
//        } else {
//            if (sessionPaymentStatus.equals(PAID_SESSION_PAYMENT_STATUS)) {
//                paymentStatus = PaymentStatus.COMPLETE;
//                orderStatus = OrderStatus.AWAITING_FULFILMENT;
//            } else {
//                paymentStatus = PaymentStatus.PENDING;
//                orderStatus = OrderStatus.AWAITING_PAYMENT;
//            }
//        }
//
//        Payment payment = Payment.builder()
//                .id(paymentId)
//                .dateTime(LocalDateTime.now())
//                .status(paymentStatus)
//                .price(BigDecimal.valueOf(orderTotalAmount, 2))
//                .order(order)
//                .build();
//        order.getPayments().add(payment);
//        order.setOrderStatus(orderStatus);
//        orderRepository.save(order);
//        AppUser user = order.getUser();
//        emailSenderService.sendEmail(user.getEmail(), user.getFirstname(), orderId, paymentStatus);
//    }
}