package com.movieMania.backend.Service;

import com.intouncommon.backend.Repository.exception.ResourceNotFoundException;
import com.movieMania.backend.Entity.movie;
import com.movieMania.backend.Entity.request;
import com.movieMania.backend.Entity.requestResponse;
import com.movieMania.backend.Repository.movieRepository;
import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class requestServiceImpl implements requestService{

    @Autowired
    private com.movieMania.backend.Repository.requestRepository requestRepository;

    @Autowired
    private otherService otherService;

    @Autowired
    private com.movieMania.backend.Repository.movieRepository movieRepository;

    private String generateCode(String contact , Long id){
        String code = "";
        if (id<10){
            contact+="000";
        }
        if (id<100){
            contact+="00";
        }
        if (id<1000){
            contact+="0";
        }
        code+=contact;
        code+=id.toString();

        return code;
    }

    @Override
    public String addRequest(request request,Long movie) {
        movie movie1 = movieRepository.findById(movie).orElseThrow(() ->
                new ResourceNotFoundException("Location", "Id", movie));
        request.setMovie(movie1);
        Long id = requestRepository.save(request).getRequestId();
        String code = generateCode(request.getContact(),id);
        Optional<request> request1 = requestRepository.findById(id);
        if (request1.isPresent()){
            request1.get().setCode(code);
            String email = request1.get().getCustomerEmail();
            requestRepository.save(request1.get());
            otherService.sendMails(email,"Your Code For Request",code);
            movie1.setRate(movie1.getRate()+1);
            movieRepository.save(movie1);
            return code;
        }
        return "error";
    }
    @Override
    public List<requestResponse> getAllPayableRequest() {
        List<request> requests = requestRepository.findAll();
        List<requestResponse> requests1 = new ArrayList<>();

        for (request request : requests){
            if (request.getPayableStatus().equalsIgnoreCase("payable")){
               requestResponse requestResponse = new requestResponse();
               requestResponse.setRequest(request);
               requestResponse.setMovie(request.getMovie());
                requests1.add(requestResponse);
            }
        }
        return requests1;
    }

    @Override
    public String cancelRequest(String code) {
        Optional<request> request = requestRepository.findByCode(code);
        if (request.isPresent()){
            if (request.get().getAdminStatus().equalsIgnoreCase("pending")){
                requestRepository.deleteByRequestId(request.get().getRequestId());
                return "cancel";
            }
            return "Cat cancel now";
        }
        return "error id";
    }

    @Override
    public String addScanCopy(String url,String code) {
        Optional<request> request = requestRepository.findByCode(code);
        if (request.isPresent()){
            request.get().setSlipUrl(url);
            request.get().setAdminStatus("payed");
            requestRepository.save(request.get());
            return "added";
        }
        return "error code";
    }

    @Override
    public List<requestResponse> getPayedRequests() {
        List<request> requests = requestRepository.findAll();
        List<requestResponse> requests1 = new ArrayList<>();
        for (request request : requests){
            if (request.getAdminStatus().equalsIgnoreCase("payed")&&request.getPayableStatus().equalsIgnoreCase("payable")){
                requestResponse requestResponse = new requestResponse();
                requestResponse.setRequest(request);
                requestResponse.setMovie(request.getMovie());
                requests1.add(requestResponse);
            }
        }
        return requests1;
    }

    @Override
    public String confirmRequest(Long id) {
        Optional<request> request = requestRepository.findById(id);
        if (request.isPresent()){
            request.get().setAdminStatus("confirm");
            String email = request.get().getCustomerEmail();
            String subject = "Your Request Has Confirmed Your movie will be uploaded soon";
            otherService.sendMails(email,"Request Confirmation",subject);
            requestRepository.save(request.get());
        }
        return "error id";
    }

    @Override
    public String rejectRequest(Long id,String reason) {
        Optional<request> request = requestRepository.findById(id);
        if (request.isPresent()) {
            if (request.get().getAdminStatus().equalsIgnoreCase("pending")) {
                String email = request.get().getCustomerEmail();
                String subject = "Your Request Has Rejected Reason - "+reason;
                otherService.sendMails(email, "Request Rejection", subject);
                requestRepository.deleteByRequestId(id);
                return "rejected";
            }
            return "cant reject";
        }

        return "error id";
    }

    @Override
    public request getByCode(String code) {
        Optional<request> request = requestRepository.findByCode(code);
        return request.orElse(null);
    }

    @Override
    public String sendUploadMail(String code) {
        Optional<request> request = requestRepository.findByCode(code);
        if (request.isPresent()){
            String subject = "Your Requested movie is uploaded to the moodle please check your driver Link - "+request.get().getDriverLink();
            otherService.sendMails(request.get().getCustomerEmail(), "Request Rejection", subject);

            return "sent";
        }
        return "error code";
    }

    @Override
    public List<requestResponse> getNotPayableRequests() {
        List<request> requests = requestRepository.findAll();
        List<requestResponse> requests1 = new ArrayList<>();

        for (request request : requests){
            if (request.getPayableStatus().equalsIgnoreCase("notPayable")&&request.getAdminShow().equalsIgnoreCase("notShow")){
                requestResponse requestResponse = new requestResponse();
                requestResponse.setRequest(request);
                requestResponse.setMovie(request.getMovie());
                requests1.add(requestResponse);
            }
        }
        return requests1;
    }

    @Override
    public String setShowState(Long id) {
        Optional<request> request = requestRepository.findById(id);
        if (request.isPresent()&&request.get().getAdminShow().equalsIgnoreCase("notShow")){
            request.get().setAdminShow("show");
            requestRepository.save(request.get());
            return "saved";
        }
        return "error id";
    }
}
