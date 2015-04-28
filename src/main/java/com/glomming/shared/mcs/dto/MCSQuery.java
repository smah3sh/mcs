package com.glomming.shared.mcs.dto;

/**
 * Container to pass in commonly used request parameters
 */
public class MCSQuery {

  public String queryString;        // Sample query: "(and (or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z'])(and points:[100, 200])(and elo_rating:[1000, 2000]))"
  public String facets;             // Sample JSON : {"weapons":{"sort":"count","size":5},"points":{"buckets":["[0,100]","[101,200]","[201,300]","[301,400]","[401,}"]}}
  public String cursor = "initial"; // Default
  public long numRecordsToReturn = 10;
}
