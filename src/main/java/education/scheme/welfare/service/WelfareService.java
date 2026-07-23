package education.scheme.welfare.service;

import education.scheme.welfare.UmisConfig;
import education.scheme.welfare.repository.WelfareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WelfareService {
    private WelfareRepository WelfareRepository;
    @Autowired
    private UmisConfig umisConfig;

    public ResponseEntity <Object> getScheme(String userId) {
        Map<String,Object> map = new HashMap<>();
        log.info(userId);

        RestTemplate restTemplate = new RestTemplate();

        String url = umisConfig.getUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", umisConfig.getAuthToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class,
                userId
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody.get("status").equals(200)) {
            if (responseBody != null && responseBody.get("result") != null) {
                Map<String, Object> studentResult = new HashMap<>();
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseBody.get("result");

                if (!resultList.isEmpty() &&  resultList.get(0).get("EMIS_ID") != null) {
                    studentResult = resultList.get(0);
                    Map<String, Object> studentDetails = new HashMap<>();
                    map.put("status", "200");
                    map.put("message", "User Id successfully fetched!");
                    map.put("timestamp", LocalDateTime.now());
                    if (studentResult != null) {
                        studentDetails.put("studentName", studentResult.get("StuName"));
                        studentDetails.put("gender", studentResult.get("Gender"));
                        studentDetails.put("studentStatus", studentResult.get("student_status"));
                        studentDetails.put("passedYear", studentResult.get("passed_year1"));
                        studentDetails.put("motherName", studentResult.get("mother_name"));
                        studentDetails.put("fatherName", studentResult.get("father_name"));
                        studentDetails.put("houseAddress", studentResult.get("house_address"));
                        studentDetails.put("community", studentResult.get("community"));
                        studentDetails.put("bloodGroup", studentResult.get("bloodgroup"));
                        studentDetails.put("schoolType", studentResult.get("school_type"));
                    }
                    map.put("studentDetails", studentDetails);
                } else {
                    map.put("status", "400");
                    map.put("message", "Response details is empty!");
                    map.put("timestamp", LocalDateTime.now());
                    map.put("studentDetails", "");
                }
            } else {
                map.put("status", "400");
                map.put("message", "Response Body is empty!");
                map.put("timestamp", LocalDateTime.now());
                map.put("studentDetails", "");
            }
        } else {
            map.put("status", "400");
            map.put("message", "Response status is empty!");
            map.put("timestamp", LocalDateTime.now());
            map.put("studentDetails", "");
        }
        return ResponseEntity.ok(map);
    }
}
