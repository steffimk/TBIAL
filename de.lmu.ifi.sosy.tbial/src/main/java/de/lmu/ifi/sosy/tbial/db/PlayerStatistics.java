package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

public class PlayerStatistics {

  public int id;
  public String name;
  public int games = 0;
  public int won = 0;
  public int lost = 0;

  public int managerCount = 0;
  public int consultantCount = 0;
  public int honestDeveloperCount = 0;
  public int evilCodeMonkeyCount = 0;

  public int bugs = 0;

  public PlayerStatistics(
      String name,
      int gcount,
      int wcount,
      int lcount,
      int mcount,
      int ccount,
      int hdcount,
      int ecmcount,
      int bcount) {
    this.name = name;
    this.games = gcount;
    this.won = wcount;
    this.lost = lcount;
    this.managerCount = mcount;
    this.consultantCount = ccount;
    this.honestDeveloperCount = hdcount;
    this.evilCodeMonkeyCount = ecmcount;
    this.bugs = bcount;
  }

  public void setName(String name) {
    this.name = requireNonNull(name);
  }

  public String getName() {
    return name;
  }

  public int getGamesCount() {
    return games;
  }

  public void setGamesCount() {
    this.games += 1;
  }

  public int getWinCount() {
    return won;
  }

  public void setWinCount() {
    this.won += 1;
  }

  public int getLoseCount() {
    return lost;
  }

  public void setLoseCount() {
    this.lost += 1;
  }

  public int getManagerCount() {
    return managerCount;
  }

  public void setManagerCount() {
    this.managerCount += 1;
  }

  public int getConsultantCount() {
    return consultantCount;
  }

  public void setConsultantCount() {
    this.consultantCount += 1;
  }

  public int getHonestDeveloperCount() {
    return honestDeveloperCount;
  }

  public void setHonestDeveloperCount() {
    this.honestDeveloperCount += 1;
  }

  public int getEvilCodeMonkeyCount() {
    return evilCodeMonkeyCount;
  }

  public void setEvilCodeMonkeyCount() {
    this.evilCodeMonkeyCount += 1;
  }

  public int getBugCount() {
    return bugs;
  }

  public void setBugCount() {
    this.bugs += 1;
  }
}
