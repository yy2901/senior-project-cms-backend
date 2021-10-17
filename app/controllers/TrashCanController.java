package controllers;

import dbconnect.APIOperations;
import dbconnect.EntryOperations;
import dbconnect.TemplateOperations;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TrashCanController extends Controller {
    private APIOperations _apiOperations;
    private EntryOperations _entryOperations;
    private TemplateOperations _templateOperations;

    @Inject
    public TrashCanController(APIOperations apiOperations, EntryOperations entryOperations, TemplateOperations templateOperations) {
        _apiOperations = apiOperations;
        _entryOperations = entryOperations;
        _templateOperations = templateOperations;
    }

    public Result getTrashCanItems() {
        Map<String, Object> map = new HashMap<>();
        map.put("routes", _apiOperations.getTrashedRoutes());
        map.put("templates", _templateOperations.getTrashedTemplates());
        map.put("entries", _entryOperations.getTrashedEntries());
        return ok(Json.toJson(map));
    }
}
