package controllers;

import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RedirectController extends Controller {
    private final HttpExecutionContext _httpExecutionContext;

    @Inject
    public RedirectController(HttpExecutionContext httpExecutionContext){
        _httpExecutionContext = httpExecutionContext;
    }

    public Result browserRouter(String path){
        return ok("Hello");
    }
}
