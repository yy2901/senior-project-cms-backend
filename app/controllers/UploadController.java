package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbconnect.UploadFiles.ArtifactOperations;
import dbconnect.UploadFiles.DetailFieldsOperations;
import dbconnect.UploadFiles.MetaOperations;
import models.Entry;
import models.UploadFiles.DetailFields;
import models.UploadFiles.Meta;
import models.UploadFiles.UpdateDetailFields;
import models.UploadFiles.UpdateMeta;
import play.libs.Files.TemporaryFile;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

@Singleton
public class UploadController extends Controller {
    private final HttpExecutionContext _httpExecutionContext;
    private final ArtifactOperations _artifactOperations;
    private final MetaOperations _metaOperations;
    private final DetailFieldsOperations _detailFieldsOperations;

    @Inject
    public UploadController(HttpExecutionContext httpExecutionContext, ArtifactOperations artifactOperations,
                            MetaOperations metaOperations, DetailFieldsOperations detailFieldsOperations){
        _httpExecutionContext = httpExecutionContext;
        _artifactOperations = artifactOperations;
        _metaOperations = metaOperations;
        _detailFieldsOperations = detailFieldsOperations;
    }

    public Result uploadFile(Http.Request request) throws IOException {
        File directory = new File("uploads");
        if(!directory.exists()) {
            directory.mkdir();
        }
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> file = body.getFile("file");
        if (file != null) {
            String fileName = file.getFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Meta meta = new Meta();
            meta.setTime(timestamp.getTime());
            meta.setSize(file.getFileSize());
            meta.setExtension(extension);
            String finalizedFileName = _artifactOperations.addArtifact(fileName, -1);
            meta.setFileName(finalizedFileName);
            _metaOperations.addMeta(meta);
            Meta createdMeta = _metaOperations.getMeta(finalizedFileName);
            long rowid = createdMeta.getRowid();
            _artifactOperations.updateArtifact(finalizedFileName,rowid);
            TemporaryFile file1 = file.getRef();
            file1.copyTo(Paths.get("uploads/"+finalizedFileName),true);
            Meta newMeta = _metaOperations.getMeta(finalizedFileName);
            return ok(Json.toJson(newMeta));
        } else {
            return badRequest("Missing File!");
        }
    }

    public Result uploadArtifact(Http.Request request, long originalFile) {
        File directory = new File("uploads");
        if(!directory.exists()) {
            directory.mkdir();
        }
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> file = body.getFile("file");
        if (file != null) {
            String finalizedFileName = _artifactOperations.addArtifact(file.getFilename(), originalFile);
            TemporaryFile file1 = file.getRef();
            file1.copyTo(Paths.get("uploads/"+finalizedFileName),true);
            return ok(finalizedFileName);
        } else {
            return badRequest("Missing File!");
        }
    }

    public Result deleteFile(long originalFile) {
        List<String> files = _artifactOperations.deleteArtifacts(originalFile);
        files.forEach(fileName->{
            File file = new java.io.File("uploads/"+fileName);
            file.delete();
        });
        return ok(_metaOperations.deleteMeta(originalFile));
    }

    public Result updateMeta(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            UpdateMeta updateMeta = mapper.treeToValue(jsonNode, UpdateMeta.class);
            String status = _metaOperations.updateMeta(updateMeta);
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result download(String name){
        return ok(new java.io.File("uploads/"+name));
    }

    public Result getUploadedFile(long id){
        return ok(Json.toJson(_metaOperations.getMeta(id)));
    }

    public Result getUploadedFiles(){
        return ok(Json.toJson(_metaOperations.getUploadedFilesMeta()));
    }

    public Result getDetailFields(String name){
        return ok(_detailFieldsOperations.getDetailFields(name));
    }
    public Result insertDetailFields(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            DetailFields detailFields = objectMapper.treeToValue(jsonNode, DetailFields.class);
            return ok(_detailFieldsOperations.insertFields(detailFields));
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }
    public Result updateDetailFields(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateDetailFields updateDetailFields = objectMapper.treeToValue(jsonNode, UpdateDetailFields.class);
            return ok(_detailFieldsOperations.updateFields(updateDetailFields));
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }
    public Result deleteDetailFields(String name) {
        return ok(_detailFieldsOperations.deleteFields(name));
    }
}
