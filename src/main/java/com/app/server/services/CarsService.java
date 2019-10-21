package com.app.server.services;

import com.app.server.http.utils.APPResponse;
import com.app.server.models.Car;
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
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class CarsService {

    private static CarsService self;
    private ObjectWriter ow;
    private MongoCollection<Document> carsCollection = null;

    private CarsService() {
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static CarsService getInstance(){
        if (self == null)
            self = new CarsService();
        return self;
    }

    public ArrayList<Car> getAll() {
        ArrayList<Car> carList = new ArrayList<Car>();

        FindIterable<Document> results = this.carsCollection.find();
        if (results == null) {
            return carList;
        }
        for (Document item : results) {
            Car car = convertDocumentToCar(item);
            carList.add(car);
        }
        return carList;
    }

    public Car getOne(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = carsCollection.find(query).first();
        if (item == null) {
            return  null;
        }
        return  convertDocumentToCar(item);
    }



    public Object update(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("make"))
                doc.append("make",json.getString("make"));
            if (json.has("model"))
                doc.append("model",json.getString("model"));
            if (json.has("color"))
                doc.append("color",json.getString("color"));
            if (json.has("size"))
                doc.append("size",json.getString("size"));
            if (json.has("year"))
                doc.append("year",json.getInt("year"));
            if (json.has("odometer"))
                doc.append("odometer",json.getString("odometer"));

            Document set = new Document("$set", doc);
            carsCollection.updateOne(query,set);
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


    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        carsCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        carsCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
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


} // end of main()
