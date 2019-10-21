package com.app.server.http;

import com.app.server.http.utils.APPListResponse;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Driver;
import com.app.server.services.DriversService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.JSONObject;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("drivers")

public class DriversHttpService {
    private DriversService service;
    private ObjectWriter ow;


    public DriversHttpService() {
        service = DriversService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @OPTIONS
    @PermitAll
    public Response optionsById() {
        return Response.ok().build();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        return new APPResponse(service.getAll());
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {

        return new APPResponse(service.getOne(id));
    }

    @GET
    @Path("{id}/cars")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getCarsForDriver(@Context HttpHeaders headers, @PathParam("id") String id, @DefaultValue("20") @QueryParam("count") int count,
                                            @DefaultValue("0") @QueryParam("offset") int offset,
                                            @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        return service.getCarsForDriver(headers, id,count,offset,sortArg);
    }

    @POST
    @Path("{id}/cars")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createCarForDriver(@PathParam("id") String id, Object request) {

        return new APPResponse(service.createCarForDriver(id, request));

    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {

        return new APPResponse(service.create(request));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){

        return new APPResponse(service.update(id,request));

    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(service.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(service.deleteAll());
    }

}
