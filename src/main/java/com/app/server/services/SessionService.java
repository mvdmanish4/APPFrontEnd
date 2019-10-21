package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPCrypt;
import com.app.server.models.Car;
import com.app.server.models.Driver;
import com.app.server.models.Session;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Services run as singletons
 */

public class SessionService {

    private static SessionService self;
    private ObjectWriter ow;
    private MongoCollection<Document> driversCollection = null;
    private MongoCollection<Document> carsCollection = null;

    private SessionService() {
        this.driversCollection = MongoPool.getInstance().getCollection("drivers");
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static SessionService getInstance(){
        if (self == null)
            self = new SessionService();
        return self;
    }

    public Session create(Object request) {

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("emailAddress"))
                throw new APPBadRequestException(55, "missing emailAddress");
            if (!json.has("password"))
                throw new APPBadRequestException(55, "missing password");
            BasicDBObject query = new BasicDBObject();

            query.put("emailAddress", json.getString("emailAddress"));
            query.put("password", APPCrypt.encrypt(json.getString("password")));

            Document item = driversCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No user found matching credentials");
            }

            Driver driver = convertDocumentToDriver(item);

            driver.setId(item.getObjectId("_id").toString());
            return new Session(driver);
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch (APPBadRequestException e) {
            throw e;
        }
        catch (APPNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
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



} // end of main()
