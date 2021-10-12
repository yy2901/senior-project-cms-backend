package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import dbconnect.APIOperations;
import models.APIRoute;
import models.UpdateAPIRoute;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CustomAPIController extends Controller {

    private final APIOperations _apiOperations;
    private final HttpExecutionContext _httpExecutionContext;

    @Inject
    public CustomAPIController (APIOperations apiOperations, HttpExecutionContext httpExecutionContext) {
        _apiOperations = apiOperations;
        _httpExecutionContext = httpExecutionContext;
    }

    /**
     * Get all customized routes
     * @return Play Result of Json response
     */
    public Result getRoutes() {
        ImmutableList<APIRoute> allRoutes = _apiOperations.getRoutes();
        return ok(Json.toJson(allRoutes));
    }

    public Result getRoute(String route) {
        APIRoute apiRoute = _apiOperations.getRoute("/"+route);
        return ok(Json.toJson(apiRoute));
    }

    public Result setRoute(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            APIRoute apiRoute = mapper.treeToValue(jsonNode, APIRoute.class);
            String status = _apiOperations.setRoute(apiRoute.getRoute());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result deleteRoute(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            APIRoute apiRoute = objectMapper.treeToValue(jsonNode, APIRoute.class);
            String status = _apiOperations.deleteRoute(apiRoute.getRoute());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result updateRoute(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateAPIRoute updateAPIRoute = objectMapper.treeToValue(jsonNode,
                    UpdateAPIRoute.class);
            String status = _apiOperations.updateRoute(updateAPIRoute);
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError(e.getMessage());
        }
    }
}
