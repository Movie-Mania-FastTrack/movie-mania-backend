package com.movieMania.backend.Controller;

import com.movieMania.backend.Entity.addUrlDto;
import com.movieMania.backend.Entity.rejectDto;
import com.movieMania.backend.Entity.request;
import com.movieMania.backend.Service.requestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
@CrossOrigin
public class RequestController {

    @Autowired
    private requestService requestService;

//    String addRequest(request request);
//    List<request> getAllRequest();
//    String cancelRequest(String code);
//    String addScanCopy(String url,String code);
//    List<request> getPayedRequests();
//    String confirmRequest(Long id);
//    String rejectRequest(Long id,String reason);
//    request getByCode(String code);
//    String sendUploadMail(String code);

    @PostMapping("/addRequest/{movieId}")
    private String addRequest(@RequestBody request request,@PathVariable Long movieId){
        return requestService.addRequest(request,movieId);
    }

    @GetMapping("/getPayableRequests")
    private List<request> getAll(){
        return requestService.getAllPayableRequest();
    }

    @GetMapping("/getNotPayableRequests")
    private List<request> getNotPayed(){
        return requestService.getNotPayableRequests();
    }

    @PutMapping("/setShow/{id}")
    private String setShow(@PathVariable Long id){
        return requestService.setShowState(id);
    }
    @GetMapping("/getPayedRequests")
    private List<request> getPayed(){
        return requestService.getPayedRequests();
    }

    @GetMapping("/getRequest/{code}")
    private request getRequest(@PathVariable String code){
        return requestService.getByCode(code);
    }

    @PutMapping("/cancelRequest/{code}")
    private String cancel(@PathVariable String code){
        return requestService.cancelRequest(code);
    }

    @PutMapping("/addScanCopy")
    private String cancel(@RequestBody addUrlDto addUrlDto){
        return requestService.addScanCopy(addUrlDto.getUrl(),addUrlDto.getCode());
    }

    @PutMapping("/confirmRequest/{id}")
    private String confirm(@PathVariable Long id){
        return requestService.confirmRequest(id);
    }

    @PutMapping("/rejectRequest")
    private String reject(@RequestBody rejectDto rejectDto){
        return requestService.rejectRequest(rejectDto.getId(),rejectDto.getReason());
    }

    @GetMapping("/sendUploadMail/{code}")
    private String sendMail(@PathVariable String code){
        return requestService.sendUploadMail(code);
    }

}
