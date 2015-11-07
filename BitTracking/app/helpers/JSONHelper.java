package helpers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.Package;
import play.libs.Json;

import java.util.List;

/**
 * Created by Mladen13 on 25.10.2015.
 */
public class JSONHelper {

    /**
     * Creating user as json
     * @param u
     * @return
     */
    public static ObjectNode jsonUser(User u) {

        ObjectNode user = Json.newObject();
        user.put("id", u.id);
        user.put("firstName", u.firstName);
        user.put("lastName", u.lastName);
        user.put("password", u.password);
        user.put("email", u.email);
        return user;

    }

    /**
     * Getting post office as json
     * @param office
     * @return
     */
    public static ObjectNode jsonPostOffice(PostOffice office) {
        ObjectNode jsonPostOffice = Json.newObject();
        jsonPostOffice.put("id", office.id);
        jsonPostOffice.put("name", office.name);
        jsonPostOffice.put("address", office.address);
        return jsonPostOffice;
    }

    /**
     * Getting package as json
     * @param pack
     * @return
     */
    public static ObjectNode jsonPackage(models.Package pack) {
        ObjectNode jsonPack = Json.newObject();
        jsonPack.put("id", pack.id);
        if(pack.trackingNum != null) {
            jsonPack.put("trackingNum", pack.trackingNum);
        }else{
            jsonPack.put("trackingNum", "Waiting For Approval");
        }
        jsonPack.put("recipientName", pack.recipientName);
        jsonPack.put("recipientAddress", pack.recipientAddress);
        jsonPack.put("weight", pack.weight);
        jsonPack.put("packageType", pack.packageType.toString());
        if (pack.shipmentPackages.size() == 0) {
            jsonPack.put("status", "REJECTED");
        } else {
            if(pack.shipmentPackages.get(0).status!=null) {
                jsonPack.put("status", pack.shipmentPackages.get(0).status.toString());
            }else{
                jsonPack.put("status", "Waiting...");
            }
        }
        if(pack.approved!=null) {
            if(pack.approved){
                jsonPack.put("approved", "Approved");
            }else{
                jsonPack.put("approved", "Rejected");
            }
        }else{
            jsonPack.put("approved", "Waiting For Approval");
        }
        if(pack.packageRejectedTimestamp!= null) {
            jsonPack.put("timestamp", pack.packageRejectedTimestamp.toString());
        }else{
            jsonPack.put("timestamp", "Waiting For Approval");
        }
        jsonPack.put("price", pack.price.toString());
        return jsonPack;
    }

    /**
     * Getting post offices list as json
     * @param offices
     * @return
     */
    public static ArrayNode jsonPostOfficeList(List<PostOffice> offices) {
        ArrayNode jsonListPostOffices = new ArrayNode(JsonNodeFactory.instance);
        for (PostOffice office : offices) {
            ObjectNode jsonOffice = jsonPostOffice(office);
            jsonListPostOffices.add(jsonOffice);
        }
        return jsonListPostOffices;
    }

    /**
     * Getting packages list as json
     * @param packages
     * @return
     */
    public static ArrayNode jsonPackagesList(List<Package> packages) {
        ArrayNode jsonListPackages = new ArrayNode(JsonNodeFactory.instance);
        for (Package pack : packages) {
            ObjectNode jsonPackage = jsonPackage(pack);
            jsonListPackages.add(jsonPackage);
        }
        return jsonListPackages;
    }

}
