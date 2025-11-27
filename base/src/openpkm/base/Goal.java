/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 *
 * @author Rok Koren
 */
public interface Goal
{    
    String getGoalID();
    String getName();
    void setName(String name); 
    String getTag();
    void setTag(String tag);    
    Level getLevel();
    void setLevel(Level level);
    boolean isAchieved();
    void setAchieved(boolean achieved);
    LocalDate getAchievedDate();
    void setAchievedDate(LocalDate date);      
    LocalDate getStartDate();
    void setStartDate(LocalDate date);  
    LocalDate getEndDate();
    void setEndDate(LocalDate date);
    String getVision();
    void setVision(String vision);
    String getAccountability();
    void setAccountability(String accountability);
    String getRewards();
    void setRewards(String rewards);
    String getObstacles();
    void setObstacles(String obstacles);
    String getSupport();
    void setSupport(String support);
    String getBrainstorming();
    void setBrainstorming(String brainstorming);       
    
    public enum Level 
    {
        SHORT_TERM("Short Term"),
        MID_TERM("Mid Term"),
        LONG_TERM("Long Term"),
        LIFE("Life");

        private String string;

        Level(String string) 
        {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
        
        public static Optional<Level> get(String string) {
            return Arrays.stream(Level.values())
                    .filter(level -> level.string.equalsIgnoreCase(string))
                    .findFirst();
        }     
    } 
    
    public static Comparator<Goal> nameComparator() 
    {
        return new Comparator<Goal>() 
        {
            @Override
            public int compare(Goal goal1, Goal goal2) 
            {
                String name1 = goal1.getName();
                String name2 = goal2.getName();
                return name1.compareTo(name2);
            }
        };
    }      
}
