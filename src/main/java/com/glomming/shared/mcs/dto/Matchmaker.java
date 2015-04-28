package com.glomming.shared.mcs.dto;

import com.wordnik.swagger.annotations.ApiModel;


@ApiModel(value = "Matchmaker class")
public class Matchmaker {

  public MatchFields fields;
  public String id;
  public String type; // Type of operation, usually "add" to add new documents

  public Matchmaker() {
    fields = new MatchFields();
  }

}
