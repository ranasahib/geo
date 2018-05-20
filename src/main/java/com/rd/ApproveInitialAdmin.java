package com.rd;

import static com.rd.utils.FreeMarker.getContent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.rd.dto.ResponseWrapper;
import com.rd.service.UserServiceImpl;
import com.rd.utils.Constant;

public class ApproveInitialAdmin {
	public static void main(String[]args){
		approveu("urn:x-indicio:csw-ebrim:def:user:admin", "User", "Basic YWRtaW46U2xpdGh5VG92ZXM=");
	}
	
	
    public static ResponseWrapper approveu(String id, String type, String token) {
        try {
            Map<String, Object> input = new HashMap<String, Object>();
            input.put("id", id);
            input.put("type", type);
            String content = getContent("approval.ftl", input);

            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/xml");
            headers.add("Authorization",token);
            HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
            String url = Constant.url + "/gcs?&outputFormat=application/json";
            try {
                ResponseEntity<Object> responseEntity = rest.exchange(url, HttpMethod.POST,
                        requestEntity, Object.class);
                return new ResponseWrapper(responseEntity.getBody());
            } catch (Exception e) {
                return new ResponseWrapper(false, "Unable to connect indicio");
            }
        } catch (Exception ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ResponseWrapper(false, "Something went wrong");
    }
}
