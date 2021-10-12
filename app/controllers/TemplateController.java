package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbconnect.TemplateOperations;
import models.Template;
import models.UpdateTemplate;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TemplateController extends Controller {
    private final TemplateOperations _templateOperations;
    private final HttpExecutionContext _httpExecutionContext;

    @Inject
    public TemplateController(TemplateOperations templateOperations, HttpExecutionContext httpExecutionContext) {
        _templateOperations = templateOperations;
        _httpExecutionContext = httpExecutionContext;
    }

    public Result getTemplate(String parent) {
        Template template = _templateOperations.getTemplate("/"+parent);
        return ok(Json.toJson(template));
    }

    public Result createTemplate(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            Template template = objectMapper.treeToValue(jsonNode, Template.class);
            String status = _templateOperations.createTemplate(template.getParent());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result deleteTemplate(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            Template template = objectMapper.treeToValue(jsonNode, Template.class);
            String status = _templateOperations.deleteTemplate(template.getRowid());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result updateTemplate(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateTemplate updateTemplate = objectMapper.treeToValue(jsonNode, UpdateTemplate.class);
            String status = _templateOperations.updateTemplate(updateTemplate);
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }
}
