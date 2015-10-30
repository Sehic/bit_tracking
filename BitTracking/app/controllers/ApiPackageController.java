package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.DijkstraHelper;
import helpers.JSONHelper;
import helpers.PackageType;
import helpers.PriceHelper;
import models.*;
import models.Package;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by Mladen13 on 25.10.2015.
 */
public class ApiPackageController extends Controller {

    public Result packageAdd(){

        JsonNode json = request().body().asJson();
        String userId = json.findPath("userId").textValue();
        String recipientName = json.findPath("recipientName").textValue();
        String recipientAddress = json.findPath("recipientAddress").textValue();
        String weight = json.findPath("weight").textValue();
        String packageType = json.findPath("packageType").textValue();
        String postOfficeName = json.findPath("postOfficeName").textValue();
        User u = User.findById(Long.parseLong(userId));
        Package pack = new Package();
        pack.recipientName = recipientName;
        pack.recipientAddress = recipientAddress;
        try {
            pack.weight = Double.parseDouble(weight);
        }catch(NumberFormatException e){
            return badRequest();
        }
        pack.senderName = u.firstName +" "+u.lastName;
        pack.price = PriceHelper.calculatePrice(pack.weight, 100);
        switch (packageType) {
            case "Box":
                pack.packageType = PackageType.BOX;
                break;
            case "Envelope":
                pack.packageType = PackageType.ENVELOPE;
                break;
            case "Flyer":
                pack.packageType = PackageType.FLYER;
                break;
            case "Tube":
                pack.packageType = PackageType.TUBE;
                break;
        }
        Shipment ship = new Shipment();
        ship.packageId = pack;
        ship.postOfficeId = PostOffice.findPostOfficeByName(postOfficeName);
        if(ship.postOfficeId == null){
            return badRequest();
        }
        u.packages.add(pack);
        pack.users.add(u);
        pack.save();
        ship.save();
        return ok();
    }


    public static Result getPackageAdd(){
        return ok(JSONHelper.jsonPostOfficeList(PostOffice.findOffice.findList()));
    }

    public Result getPackageList(){
        Logger.info("uso");
        List<Package> packs = Package.finder.findList();

        return ok(JSONHelper.jsonPackagesList(packs));
    }

    public Result jsonLocation(String loc) {
        String[] locs = loc.split("&");
        Location newLocation = new Location(Double.parseDouble(locs[0]), Double.parseDouble(locs[1]));
        List<PostOffice> offices = PostOffice.findOffice.findList();

        PostOffice office = offices.get(0);
        Location location = offices.get(0).place;

        double distance = DijkstraHelper.getDistance(location.toString(), newLocation.toString());

        for (int i = 1; i < offices.size(); i++) {
            double newDistance = DijkstraHelper.getDistance(offices.get(i).place.toString(), newLocation.toString());
            if (newDistance < distance) {
                distance = newDistance;
                office = offices.get(i);
                location = offices.get(i).place;
            }
        }
        String androidLoc = location.toString() + "," + office.name;

        return ok(androidLoc);
    }

}
