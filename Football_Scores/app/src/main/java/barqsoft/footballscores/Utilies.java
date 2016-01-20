package barqsoft.footballscores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return context.getResources().getString(R.string.seriaa);
            case PREMIER_LEGAUE : return context.getResources().getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE : return context.getResources().getString(R.string.premierleague);
            case PRIMERA_DIVISION : return context.getResources().getString(R.string.primeradivison);
            case BUNDESLIGA : return context.getResources().getString(R.string.bundesliga);
            default: return context.getResources().getString(R.string.unknown_league);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getResources().getString(R.string.group_stage_text) + ", " +
                        context.getResources().getString(R.string.matchday_text) + " : 6";
                //"Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getResources().getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getResources().getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getResources().getString(R.string.semi_final);
            }
            else
            {
                return context.getResources().getString(R.string.final_text);
            }
        }
        else
        {
            return context.getResources().getString(R.string.matchday_text) + " : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }


    public static int getTeamCrestByTeamName (Context context, String teamname)
    {

        if (teamname==null){return R.drawable.no_icon;}

        if (teamname.equals(context.getResources().getString(R.string.arsenal))) {
            return R.drawable.arsenal;
        } else if (teamname.equals(context.getResources().getString(R.string.swansea_city_fc))) {
            return R.drawable.swansea_city_afc;
        } else if (teamname.equals(context.getResources().getString(R.string.manchester_united_fc))) {
            return R.drawable.manchester_united;
        }else if (teamname.equals(context.getResources().getString(R.string.leicester_city))) {
            return R.drawable.leicester_city_fc_hd_logo;
        }else if (teamname.equals(context.getResources().getString(R.string.everton_fc))) {
            return R.drawable.everton_fc_logo1;
        }else if (teamname.equals(context.getResources().getString(R.string.west_ham_united_fc))) {
            return R.drawable.west_ham;
        }else if (teamname.equals(context.getResources().getString(R.string.tottenham_hotspur_fc))) {
            return R.drawable.tottenham_hotspur;
        }else if (teamname.equals(context.getResources().getString(R.string.west_bromwich_albion))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        }else if (teamname.equals(context.getResources().getString(R.string.sunderland_afc))) {
            return R.drawable.sunderland;
        }else if (teamname.equals(context.getResources().getString(R.string.stoke_city_fc))) {
            return R.drawable.stoke_city;
        } else {
            return R.drawable.no_icon;
        }
    }
    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
