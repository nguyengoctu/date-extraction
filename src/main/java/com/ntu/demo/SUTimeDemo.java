package com.ntu.demo;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SUTimeDemo {
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static void main(String[] args) {

        String text = "\"Personal page for Marvin Minsky\". web.media.mit.edu. Retrieved 23 June 2016.\n" +
                "\n" +
                "Admin (January 27, 2016). \"Official Alcor Statement Concerning Marvin Minsky\".\n" +
                "\n" +
                "Alcor Life Extension Foundation. Retrieved 2016-04-07.\n" +
                "\n" +
                "\"IEEE Computer Society Magazine Honors Artificial Intelligence Leaders\".\n" +
                "\n" +
                "DigitalJournal.com. August 24, 2011. Retrieved September 18, 2011.\n" +
                "\n" +
                "Press release source: PRWeb (Vocus).\n" +
                "\n" +
                "\"Dan David prize 2014 winners\". May 15, 2014. Retrieved May 20, 2014.";

        List<LocalDate> extractedDates = extractDate(text);
        displayDates(extractedDates);
    }


    /**
     * Extract dates using Stanford NLP
     * @param text input string that contains dates
     * @return a list of LocalDate objects
     */
    private static List<LocalDate> extractDate(String text) {
        Properties properties = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", properties));

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<LocalDate> dates = new ArrayList<>();
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

        for (CoreMap cm : timexAnnsAll) {
            LocalDate date = stringToDate(cm.get(TimeExpression.Annotation.class).getTemporal().toString());
            dates.add(date);
        }
        Collections.sort(dates);
        return dates;
    }


    /**
     * convert string to LocalDate format
     * @param s string
     * @return a LocalDate object
     */
    private static LocalDate stringToDate(String s){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDate.parse(s, formatter);
    }


    /**
     * display extracted and sorted dates with given format
     * @param dates extracted and sorted dates
     */
    private static void displayDates(List<LocalDate> dates) {
        int tmpYear = Integer.MAX_VALUE;
        int tmpMonth = Integer.MAX_VALUE;
        int counter = 0;

        for (LocalDate date : dates) {
            if (date.getYear() != tmpYear) {
                counter = 1;
                tmpYear = date.getYear();
                System.out.println(tmpYear + ":");

                tmpMonth = Integer.MAX_VALUE;
            } else {
                counter += 1;

            }

            if (date.getMonthValue() != tmpMonth) {
                tmpMonth = date.getMonthValue();
                System.out.println("-" + tmpMonth);
            }

            System.out.println("-" + date.getDayOfMonth() + "(" + counter + ")");
        }
    }
}
