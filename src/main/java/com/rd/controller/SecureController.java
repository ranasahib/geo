package com.rd.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rd.dto.ComplexQueryDTO;
import com.rd.dto.HarvestDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;
import com.rd.dto.transaction.Response;
import com.rd.service.CommonService;
import com.rd.service.ComplexQueryServiceImpl;
import com.rd.service.MapService;
import com.rd.service.OrganisationService;
import com.rd.service.QueryService;
import com.rd.service.UserService;

@Controller
@RequestMapping("/secure")
public class SecureController {

    @Autowired
    CommonService commonService;

    @Autowired
    UserService userService;

    @Autowired
    OrganisationService organisationService;
    
    @Autowired
    MapService mapService;
    
    @Autowired
    QueryService queryService;
    
    @Autowired
    ComplexQueryServiceImpl complexQueryService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String sayHello() {
        return "Secure Hello!";
    }

    @RequestMapping(value = {"/user"})
    public ResponseEntity<?> token() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(userDetailDTO, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/approve/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<?> approve(@PathVariable String id) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.approveUser(id, userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/query"}, method = RequestMethod.GET)
    public ResponseEntity<?> query(@RequestParam(required = false) String request, @RequestParam(required = false) String service, @RequestParam(required = false) String version, @RequestParam(required = false) String id, @RequestParam(required = false) String outputFormat, @RequestParam(required = false) String elementSetName, @RequestParam(required = false) String qid, @RequestParam(required = false) String resultType, @RequestParam(required = false) String maxRecords, @RequestParam(required = false) String type, @RequestParam(required = false) String startPosition, @RequestParam(required = false) String userId) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = commonService.query(userDetailDTO, request, service, version, id, outputFormat, elementSetName, qid, resultType, maxRecords, type, startPosition, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/delete"}, method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestBody List<String> ids) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = commonService.delete(ids, userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper.getData(), headers, HttpStatus.OK);
    }

    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/createUser"}, method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> input) throws Exception {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        input.put("id", randomUUIDString);
        if (input.get("middleName") == null) {
            input.put("middleName", "");
        }
        if (input.get("password") == null) {
            input.put("password", "testing");
        }

        Response[] output = commonService.operationResponse("gcs", input, "createUser", userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(output, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/listUser"}, method = RequestMethod.GET)
    public ResponseEntity<?> listUser() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listUser(userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/submitUser/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<?> submitUser(@PathVariable String id) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.submitUser(id,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/addUser"}, method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.addUser(userDTO,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/harvest"}, method = RequestMethod.POST)
    public ResponseEntity<?> harvest(@RequestBody HarvestDTO harvestDTO) throws Exception {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = commonService.harvestValidate(harvestDTO,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/harvestSync/{harvestId}"}, method = RequestMethod.POST)
    public ResponseEntity<?> harvestSync(@PathVariable Long harvestId) throws Exception {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = commonService.harvestSyncValidate(harvestId,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/listUser/activated"}, method = RequestMethod.GET)
    public ResponseEntity<?> listActivatedUser() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listUser(1,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/listUser/submitted"}, method = RequestMethod.GET)
    public ResponseEntity<?> listActivatedSubmitted() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listUser(2,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/listUser/approved"}, method = RequestMethod.GET)
    public ResponseEntity<?> listActivatedApproved() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listUser(3,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/map/view"}, method = RequestMethod.POST)
    public ResponseEntity<?> createMapView(@RequestBody JSONObject request) {
        
        ResponseWrapper responseWrapper = mapService.mapView(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/query/{type}"}, method = RequestMethod.POST)
    public ResponseEntity<?> query(@RequestBody Map<String, Object> queryDto,@PathVariable String type) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = null;
        responseWrapper = queryService.search(queryDto,userDetailDTO,type);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/classification/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<?> query(@PathVariable String id) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.addClassificationToUser(id, userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    

    
    @RequestMapping(value = {"/list/classification/{type}"}, method = RequestMethod.POST)
    public ResponseEntity<?> listClassification(@PathVariable String type) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listAssociation(type);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/list/harvest"}, method = RequestMethod.GET)
    public ResponseEntity<?> listHarvest() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.listHarvest(userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/sync/harvest/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<?> syncHarvest(@PathVariable Long id) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.syncHarvest(id,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/complexQuery"}, method = RequestMethod.POST)
    public ResponseEntity<?> complexQuery(@RequestBody ComplexQueryDTO queryData) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = complexQueryService.complexQuery(queryData,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }

}
