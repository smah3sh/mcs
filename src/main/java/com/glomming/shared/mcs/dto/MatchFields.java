package com.glomming.shared.mcs.dto;

import com.wordnik.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;


@ApiModel(value = "MatchFields class")
public class MatchFields {

  public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  public Float elo_rating;
  public int randomizer;
  public int points;
  public String last_login;  // Last login time formatted as a string
  public List<String> weapons;

  public MatchFields() {
  }
}
