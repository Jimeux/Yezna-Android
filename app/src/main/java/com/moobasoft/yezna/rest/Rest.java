package com.moobasoft.yezna.rest;

public interface Rest {

    int MAX_STALE = 60 * 60 * 24 * 28;

    String PRODUCTION_API_URL = "http://10.4.108.5:3000/";
    //String PRODUCTION_API_URL = "http://192.168.11.5:3000/";

    String ACCEPT_HEADER = "Accept";
    String ACCEPT_JSON = "application/javascript, application/json";

    String AUTHORIZATION_HEADER = "Authorization";
    String BEARER = "Bearer ";

    String CACHE_CONTROL_HEADER = "Cache-Control";
    String CACHE_DEFAULT = null;
    String CACHE_NO_CACHE = "no-cache, no-store, must-revalidate";
    String CACHE_ONLY_IF_CACHED = "public, only-if-cached, max-stale=" + MAX_STALE;

}
