package com.rupp.assignment.controller;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.rupp.assignment.json.BootstrapTableModel;
import com.rupp.assignment.json.JEnrollment;
import com.rupp.assignment.json.JMessage;
import com.rupp.assignment.json.JMessage.MessageType;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = {"enrollment", "enrollments" })
public class EnrollmentController {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentController.class);
    @Autowired
    private com.rupp.assignment.service.EnrollmentService service;
    @Autowired
    private JMessage message;


    @RequestMapping(value = "v1/all", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="Get all enrollments by paging", notes = "Get all enrollments by paging", response = JEnrollment.class, responseContainer = "List")
    public BootstrapTableModel<JEnrollment> getAll(HttpServletRequest request, WebRequest webRequest,
            @RequestHeader(required = false, value = "If-Modified-Since") Date since) {

    	BootstrapTableModel<JEnrollment> res = new BootstrapTableModel<JEnrollment>();
    	String search = request.getParameter("search");
    	int limit = Integer.parseInt(request.getParameter("limit"));
    	int offset = Integer.parseInt(request.getParameter("offset"));
    	res.setRows(service.getPage(limit, offset, search));
    	res.setTotal(service.count(search));
        return res;
    }

    @RequestMapping(value = "v1/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="Get enrollment by id", notes = "Get enrollment by id", response = JEnrollment.class)
    public JEnrollment getDetails(HttpServletRequest request, @PathVariable int id) {
        return service.getDetails(id);
    }

    @RequestMapping(value = "v1", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="Create enrollment", notes = "Create enrollment", response = JMessage.class)
    public JMessage create(HttpServletRequest request, @ModelAttribute JEnrollment domain) {

    	if(domain.getCourseId() == 0 ||
    		domain.getRoomId() == 0 ||
    		domain.getEnrollmentDate() == null ){
    		this.message.setMessage("Please fill all require fields!");
    		this.message.setStatus(MessageType.ERROR);
    		return this.message;
    	}
    	
    	// check if course enrolled
    	JEnrollment existedEnrollment = service.findEnrolledCourse(domain.getStudentId(), domain.getCourseId());
    	if(existedEnrollment != null){
    		this.message.setMessage("This student has already enrolled that course");
    		this.message.setStatus(MessageType.ERROR);
    		return this.message;
    	}    	
        return service.create(domain);
    }
    
    @RequestMapping(value = "v1/{id}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="Update enrollment", notes = "Update enrollment", response = JMessage.class)
    public JMessage update(HttpServletRequest request, @PathVariable int id, @ModelAttribute JEnrollment domain) {
    	
    	if(domain.getCourseId() == 0 ||
    		domain.getRoomId() == 0 ||
    		domain.getEnrollmentDate() == null ){
    		this.message.setMessage("Please fill all require fields!");
    		this.message.setStatus(MessageType.ERROR);
    		return this.message;
    	}
    	
    	// check if course enrolled
    	JEnrollment existedEnrollment = service.findEnrolledCourseExcludeEnrollment(domain.getStudentId(), domain.getCourseId(), id);
    	if(existedEnrollment != null){
    		this.message.setMessage("This student has already enrolled that course");
    		this.message.setStatus(MessageType.ERROR);
    		return this.message;
    	}    	
        
        return service.update(id, domain);
    }
    
    @RequestMapping(value = "v1/remove", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="Remove enrollment", notes = "Remove enrollment", response = JMessage.class)
    public JMessage remove(HttpServletRequest request) {
        return service.remove(Integer.parseInt(request.getParameter("id")));
    }
    
 
}
