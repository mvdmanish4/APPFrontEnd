package com.app.client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {
    public static void main(String[] argv) {
        doDeleteAll();
        doPost("James", "", "Madison","34 Main Street", "", "SuperTown", "AZ", "us", "67363");
        doPost("Sue", "", "Mann","67 Fifth Street", "", "Anytown", "CA", "us", "62723");
        doPost("Peter", "Q", "Pearson","6733 Fallen Leaf Street", "", "MagicTown", "ON", "ca", "56TY87");
        doGetAll();
    }

    public static void doPost(String firstName,String middleName, String lastName, String address1, String address2,
                              String city, String state, String country, String postalCode){
        try {
            URL url = new URL("http://localhost:8080/api/drivers");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoInput(true);

            con.setDoOutput(true);

            JSONObject driver = new JSONObject();
            driver.put("firstName",firstName);
            driver.put("middleName",middleName);
            driver.put("lastName",lastName);
            driver.put("address1",address1);
            driver.put("address2",address2);
            driver.put("city",city);
            driver.put("state",state);
            driver.put("country",country);
            driver.put("postalCode",postalCode);

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(driver.toString());
            wr.flush();


            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        }
        catch(Exception e) {
            e.printStackTrace();

        }

    }


    public static void doGetAll() {
        try {
            URL url = new URL("http://localhost:8080/api/drivers");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void doDeleteAll() {
        try {
            URL url = new URL("http://localhost:8080/api/drivers");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }






}
