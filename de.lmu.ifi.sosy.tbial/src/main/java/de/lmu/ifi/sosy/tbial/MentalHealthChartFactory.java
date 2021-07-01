package de.lmu.ifi.sosy.tbial;

import java.io.Serializable;
import java.util.Set;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Generics;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.dataviz.chart.Chart;
import com.googlecode.wicket.kendo.ui.dataviz.chart.series.LineSeries;
import com.googlecode.wicket.kendo.ui.dataviz.chart.series.Series;

import de.lmu.ifi.sosy.tbial.game.Player;

/**
 * Provides the line chart displaying the development of the mental health points of all players
 * that is needed at the end of a game.
 */
public class MentalHealthChartFactory implements Serializable {

  private static final long serialVersionUID = 1L;

  private final Set<Player> players;
  private final int numberOfDataPerPlayer;

  public MentalHealthChartFactory(Set<Player> players, int numberOfDataPerPlayer) {
    this.players = players;
    this.numberOfDataPerPlayer = numberOfDataPerPlayer;
  }

  /**
   * Returns a new instance of Chart
   *
   * @param wicketId The id of the component
   * @return The chart component.
   */
  public Chart<MentalHealthData> getNewChartInstance(String wicketId) {
    return new Chart<MentalHealthData>(wicketId, newModel(), newSeries(), newOptions());
  }

  static Options newOptions() {
    Options options = new Options();
    options.set("title", "{ text: 'Development of Mental Health Points' }");
    options.set("legend", "{ position: 'top' }");
    options.set(
        "tooltip",
        "{ visible: true, template: '#= series.name #: #= kendo.toString(value, \"n0\") #' }");
    options.set("categoryAxis", "{ field: 'category' }"); // MentalHealthData#category field

    return options;
  }

  private List<Series> newSeries() {
    List<Series> series = Generics.newArrayList();
    int i = 1;
    for (Player player : players) {
      String field;
      switch (i) {
        case 1:
          field = MentalHealthData.PLAYER_1;
          break;
        case 2:
          field = MentalHealthData.PLAYER_2;
          break;
        case 3:
          field = MentalHealthData.PLAYER_3;
          break;
        case 4:
          field = MentalHealthData.PLAYER_4;
          break;
        case 5:
          field = MentalHealthData.PLAYER_5;
          break;
        case 6:
          field = MentalHealthData.PLAYER_6;
          break;
        default:
          field = MentalHealthData.PLAYER_7;
          break;
      }
      series.add(new LineSeries(player.getUserName(), field));
      i++;
    }
    return series;
  }

  private IModel<List<MentalHealthData>> newModel() {
    return new LoadableDetachableModel<List<MentalHealthData>>() {

      private static final long serialVersionUID = 1L;

      @Override
      protected List<MentalHealthData> load() {
        return getLines();
      }
    };
  }

  private List<MentalHealthData> getLines() {
    List<MentalHealthData> data = Generics.newArrayList();

    for (int round = 0; round < numberOfDataPerPlayer; round++) {
      Integer[] values = new Integer[7];
      int i = 0;
      for (Player player : players) {
        values[i] = player.getMentalHealthOfRound(round);
        i++;
      }
      data.add(new MentalHealthData("#" + round, values));
    }
    return data;
  }

  // classes //

  public class MentalHealthData implements IClusterable {
    private static final long serialVersionUID = 1L;

    public static final String PLAYER_1 = "value1";
    public static final String PLAYER_2 = "value2";
    public static final String PLAYER_3 = "value3";
    public static final String PLAYER_4 = "value4";
    public static final String PLAYER_5 = "value5";
    public static final String PLAYER_6 = "value6";
    public static final String PLAYER_7 = "value7";

    private final Integer[] values;

    private final String category;

    public MentalHealthData(String category, Integer[] values) {
      this.category = category;
      this.values = values;
    }

    public String getCategory() {
      return this.category;
    }

    public Integer getValue1() {
      return this.values[0];
    }

    public Integer getValue2() {
      return this.values[1];
    }

    public Integer getValue3() {
      return this.values[2];
    }

    public Integer getValue4() {
      return this.values[3];
    }

    public Integer getValue5() {
      return this.values[4];
    }

    public Integer getValue6() {
      return this.values[5];
    }

    public Integer getValue7() {
      return this.values[6];
    }
  }
}
