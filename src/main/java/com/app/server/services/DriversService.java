package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPCrypt;
import com.app.server.http.utils.APPListResponse;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Car;
import com.app.server.models.Driver;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Services run as singletons
 */

public class DriversService {

    private static DriversService self;
    private ObjectWriter ow;
    private MongoCollection<Document> driversCollection = null;
    private MongoCollection<Document> carsCollection = null;

    private DriversService() {
        this.driversCollection = MongoPool.getInstance().getCollection("drivers");
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static DriversService getInstance(){
        if (self == null)
            self = new DriversService();
        return self;
    }

    public ArrayList<Driver> getAll() {
        ArrayList<Driver> driverList = new ArrayList<Driver>();

        FindIterable<Document> results = this.driversCollection.find();
        if (results == null) {
            return driverList;
        }
        for (Document item : results) {
            Driver driver = convertDocumentToDriver(item);
            driverList.add(driver);
        }
        return driverList;
    }

    public Driver getOne(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = driversCollection.find(query).first();
        if (item == null) {
            return  null;
        }
        return  convertDocumentToDriver(item);
    }

    public Driver create(Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Driver driver = convertJsonToDriver(json);
            Document doc = convertDriverToDocument(driver);
            driversCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            driver.setId(id.toString());
            return driver;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object update(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("firstName"))
                doc.append("firstName",json.getString("firstName"));
            if (json.has("middleName"))
                doc.append("middleName",json.getString("middleName"));
            if (json.has("lastName"))
                doc.append("lastName",json.getString("lastName"));
            if (json.has("address1"))
                doc.append("address1",json.getString("address1"));
            if (json.has("address2"))
                doc.append("address2",json.getString("address2"));
            if (json.has("city"))
                doc.append("city",json.getString("city"));
            if (json.has("state"))
                doc.append("state",json.getString("state"));
            if (json.has("country"))
                doc.append("country",json.getString("country"));
            if (json.has("postalCode"))
                doc.append("postalCode",json.getString("postalCode"));

            Document set = new Document("$set", doc);
            driversCollection.updateOne(query,set);
            return request;

        } catch(JSONException e) {
            System.out.println("Failed to update a document");
            return null;


        }
        catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public APPListResponse getCarsForDriver(HttpHeaders headers, String id, int count, int offset, String sortArg) {

        try {
            checkAuthentication(headers, id);

            ArrayList<Car> carsList = new ArrayList<Car>();
            BasicDBObject query = new BasicDBObject();
            query.put("driverId", id);

            BasicDBObject sortParams = new BasicDBObject();
            List<String> sortList = Arrays.asList(sortArg.split(","));
            sortList.forEach(sortItem -> {
                sortParams.put(sortItem, 1);
            });

            long resultCount = carsCollection.count(query);

            FindIterable<Document> results = this.carsCollection.find().sort(sortParams).skip(offset).limit(count);
            if (results == null) {
                return new APPListResponse(carsList, resultCount, offset, carsList.size());
            }
            for (Document item : results) {
                Car car = convertDocumentToCar(item);
                carsList.add(car);
            }

            return new APPListResponse(carsList, resultCount, offset, carsList.size());
        }
        catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }
        catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }

    }

    public Car createCarForDriver(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Car car = convertJsonToCar(json, id);
            Document doc = convertCarToDocument(car);
            carsCollection.insertOne(doc);
            ObjectId carId = (ObjectId)doc.get( "_id" );
            car.setId(carId.toString());
            return car;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        driversCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        driversCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private Driver convertDocumentToDriver(Document item) {
        Driver driver = new Driver(
                item.getString("firstName"),
                item.getString("middleName"),
                item.getString("lastName"),
                item.getString("address1"),
                item.getString("address2"),
                item.getString("city"),
                item.getString("state"),
                item.getString("country"),
                item.getString("postalCode"),
                item.getString("emailAddress")
        );
        driver.setId(item.getObjectId("_id").toString());
        return driver;
    }

    private Document convertDriverToDocument(Driver driver){
        Document doc = new Document("firstName", driver.getFirstName())
                .append("middleName", driver.getMiddleName())
                .append("lastName", driver.getLastName())
                .append("address1", driver.getAddress1())
                .append("address2", driver.getAddress2())
                .append("city", driver.getCity())
                .append("state", driver.getState())
                .append("country", driver.getCountry())
                .append("postalCode", driver.getPostalCode())
                .append("emailAddress", driver.getEmailAddress());
        return doc;
    }

    private Driver convertJsonToDriver(JSONObject json){
        Driver driver = new Driver( json.getString("firstName"),
                json.getString("middleName"),
                json.getString("lastName"),
                json.getString("address1"),
                json.getString("address2"),
                json.getString("city"),
                json.getString("state"),
                json.getString("country"),
                json.getString("postalCode"),
                json.getString("emailAddress"));
        return driver;
    }

    private Document convertCarToDocument(Car car){
        Document doc = new Document("make", car.getMake())
                .append("model", car.getModel())
                .append("year", car.getYear())
                .append("size", car.getSize())
                .append("color", car.getColor())
                .append("odometer", car.getOdometer())
                .append("driverId", car.getDriverId());
        return doc;
    }

    private Car convertJsonToCar(JSONObject json, String id){
        Car car = new Car( json.getString("make"),
                json.getString("model"),
                json.getInt("year"),
                json.getString("size"),
                json.getString("color"),
                json.getInt("odometer"),
                id);
        return car;
    }

    private Car convertDocumentToCar(Document item) {
        Car driver = new Car(
                item.getString("make"),
                item.getString("model"),
                item.getInteger("year", -1),
                item.getString("size"),
                item.getString("color"),
                item.getInteger("odometer"),
                item.getString("driverId")
        );
        driver.setId(item.getObjectId("_id").toString());
        return driver;
    }

    void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }




} // end of main()
