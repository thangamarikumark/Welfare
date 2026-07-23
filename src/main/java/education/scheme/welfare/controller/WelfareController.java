package education.scheme.welfare.controller;

import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import education.scheme.welfare.models.ExceptionEntity;
import education.scheme.welfare.service.WelfareService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/scheme")
public class WelfareController {
    private static final Logger logger = LoggerFactory.getLogger(WelfareController.class);

    @Autowired
    private WelfareService welfareService;

    @PostMapping("/welfare")
    public ResponseEntity<Object> GetScheme(@RequestBody Map<String, String> request) {
        String userId = request.get("user_id");
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("user_id is required");
        } else {
            return welfareService.getScheme(userId);
        }
    }

    @PostMapping("/signing-encryption")
    public ResponseEntity<Object> SigningEncryption(@RequestBody String request) {
        try {
            logger.info("Received Request:\n" + request);
            JSONObject obj = new JSONObject(request);
            String encryptionKey = obj.getString("encryptionKey");
            String keyId = obj.getString("keyId");
            String clientId = obj.getString("clientId");
            JSONObject responseString = obj.getJSONObject("payload");
            String PayloadString = responseString.toString();
            Map<String, Object> response = new HashMap<>();

            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).keyID(keyId)
                    .customParam("clientid", clientId).build();
            Payload payload = new Payload(PayloadString);
            JWEObject jweObject = new JWEObject(jweHeader, payload);
            jweObject.encrypt(new DirectEncrypter(key));
            String jweEncryptedData = jweObject.serialize();

            logger.info("Encrypted Request:\n" + jweEncryptedData);

            // Signed Request
            JWSSigner jwsSigner = new MACSigner(encryptionKey);
            Map<String, Object> customParams = new HashMap<>();
            customParams.put("clientid", clientId);
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .keyID(keyId)
                    .customParams(customParams)
                    .build();

            JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(jweEncryptedData));
            // Sign the JWS
            jwsObject.sign(jwsSigner);

            String signedRequest = jwsObject.serialize();

            logger.info("Signing Request:\n{}", signedRequest);

            response.put("encrypted_value", jweEncryptedData);
            response.put("signing_value", jwsObject.serialize());
            response.put("error_code", "1");
            response.put("error_message", "Successfully Encrypted");
            logger.info("Response:\n" + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            logger.info("Error occurred while encrypting request" + e.getMessage());
            response.put("encrypted_value", "");
            response.put("signing_value", "");
            response.put("error_code", "0");
            response.put("error_message", e.getMessage());
            logger.info("Response:\n" + response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/signing-decryption")
    public ResponseEntity<Object> SigningDecryption(@RequestBody String request) {
        try {
            logger.info("Received Request:\n" + request);
            JSONObject obj = new JSONObject(request);
            String encryptionKey = obj.getString("encryptionKey");
            String responseString = obj.getString("encryptedPayload");
            Map<String, Object> response = new HashMap<>();

            String[] Split = responseString.split("\\.");
            String ReqPay = Split[1];
            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            byte[] decodedBytes = Base64.decodeBase64(ReqPay);
            String decodedString = new String(decodedBytes);
            JWEObject parsedJWE = JWEObject.parse(decodedString);
            parsedJWE.decrypt(new DirectDecrypter(key));
            String decryptedPayload = parsedJWE.getPayload().toString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonPayload = mapper.readValue(decryptedPayload, Map.class);

            logger.info("Decrypted Response:\n" + jsonPayload);
            response.put("decrypted_value", jsonPayload);
            response.put("error_code", "1");
            response.put("error_message", "Successfully Decrypted");
            logger.info("Response:\n" + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            logger.info("Error occurred while encrypting request" + e.getMessage());
            response.put("decrypted_value", "");
            response.put("error_code", "0");
            response.put("error_message", e.getMessage());
            logger.info("Response:\n" + response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
