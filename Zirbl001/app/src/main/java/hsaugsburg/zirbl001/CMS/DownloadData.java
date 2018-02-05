package hsaugsburg.zirbl001.CMS;

import android.app.Activity;
import android.util.Log;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hsaugsburg.zirbl001.CMS.LoadTasks.LoadDoUKnow;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadIdentifySound;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadLetters;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadPictureCountdown;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadQuiz;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadSlider;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadTrueFalse;
import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.Models.TourModels.IdentifySoundModel;
import hsaugsburg.zirbl001.Models.TourModels.LettersModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.StationModel;
import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;
import hsaugsburg.zirbl001.Models.TourModels.QuizModel;
import hsaugsburg.zirbl001.Models.TourModels.SliderModel;
import hsaugsburg.zirbl001.Models.TourModels.TaskModel;
import hsaugsburg.zirbl001.Models.TourModels.TrueFalseModel;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DownloadData {
    private String selectedTour;
    private Activity activity;
    private DownloadActivity downloadActivity;
    private int totalAmountOfLetters = 14;

    public DownloadData(Activity activity, DownloadActivity downloadActivity, String selectedTour) {
        this.selectedTour = selectedTour;
        this.downloadActivity = downloadActivity;
        this.activity = activity;
    }

    public void downloadData() {
        CDAClient client = CDAClient.builder()
                .setSpace("age874frqdcf")
                .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                .build();

        client.observe(CDAEntry.class)
                .where("content_type", "rChronology")
                .where("fields.tourId.sys.id", selectedTour)
                .all()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CDAArray>() {
                    CDAArray result;

                    @Override
                    public void onCompleted() {
                        ArrayList<ChronologyModel> chronologyModels = new ArrayList<>();

                        ArrayList<StationModel> stations = new ArrayList<>();
                        ArrayList<DoUKnowModel> infoPopups = new ArrayList<>();

                        ArrayList<IdentifySoundModel> identifySoundModels = new ArrayList<>();
                        ArrayList<LettersModel> lettersModels = new ArrayList<>();
                        ArrayList<PictureCountdownModel> pictureCountdownModels = new ArrayList<>();
                        ArrayList<QuizModel> quizModels = new ArrayList<>();
                        ArrayList<SliderModel> sliderModels = new ArrayList<>();
                        ArrayList<TrueFalseModel> trueFalseModels = new ArrayList<>();

                        for (CDAResource resource : result.items()) {
                            ChronologyModel chronologyModel = new ChronologyModel();
                            CDAEntry entry = (CDAEntry) resource;
                            chronologyModel.setTourContentfulID(entry.id());
                            int chronologyNumber = Double.valueOf(entry.getField("chronologyNumber").toString()).intValue();
                            chronologyModel.setChronologyNumber(chronologyNumber);

                            if (entry.getField("stationId") != null) {
                                CDAEntry station = (CDAEntry) entry.getField("stationId");
                                chronologyModel.setStationContentfulID(station.id());
                                StationModel stationModel = new StationModel();
                                stationModel.setContentfulID(station.id());
                                stationModel.setChronologyNumber(chronologyNumber);
                                stationModel.setTourContentfulID(selectedTour);
                                stationModel.setStationName(station.getField("stationname").toString());

                                Log.d("Contentful Stations Test", station.getField("stationname").toString());


                                String location = station.getField("location").toString();
                                String parts[] = location.split(",");
                                double latitude = Double.valueOf(parts[0].substring(5));
                                double longitude = Double.valueOf(parts[1].substring(5, parts[1].length() - 1));
                                stationModel.setLatitude(latitude);
                                stationModel.setLongitude(longitude);

                                stationModel.setMapInstruction(station.getField("mapInstruction").toString());


                                String wayPointsString = station.getField("wayPoints").toString();
                                try {
                                    JSONArray wayPointsJson = new JSONArray(wayPointsString);
                                    stationModel.setWayPoints(wayPointsJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                stations.add(stationModel);


                            } else if (entry.getField("taskId") != null) {
                                CDAEntry task = (CDAEntry) entry.getField("taskId");
                                chronologyModel.setTourContentfulID(selectedTour);
                                chronologyModel.setTaskContentfulID(task.id());
                                String contentType = task.contentType().id();
                                chronologyModel.setTaskClassName(contentType);
                                TaskModel taskModel = new TaskModel();
                                int taskValue = -1;

                                if (contentType.equals("eSingleChoiceTask")) {
                                    taskModel = new QuizModel();
                                    ((QuizModel) taskModel).setRightAnswer(task.getField("rightAnswer").toString());
                                    ((QuizModel) taskModel).setOption2(task.getField("option2").toString());
                                    ((QuizModel) taskModel).setOption3(task.getField("option3").toString());

                                    if (task.getField("option4") != null) {
                                        ((QuizModel) taskModel).setOption4(task.getField("option4").toString());
                                    }

                                    taskValue = 0;
                                } else if (contentType.equals("eGuessTheNumberTask")) {
                                    taskModel = new SliderModel();

                                    ((SliderModel) taskModel).setRightNumber(Double.valueOf(task.getField("rightNumber").toString()));
                                    ((SliderModel) taskModel).setMinRange(Double.valueOf(task.getField("minRange").toString()));
                                    ((SliderModel) taskModel).setMaxRange(Double.valueOf(task.getField("maxRange").toString()));
                                    ((SliderModel) taskModel).setToleranceRange(Double.valueOf(task.getField("toleranceRange").toString()).intValue());
                                    ((SliderModel) taskModel).setIsInteger(Boolean.parseBoolean(task.getField("isInteger").toString()));

                                    taskValue = 1;

                                } else if (contentType.equals("eIdentifySoundTask")) {
                                    taskModel = new IdentifySoundModel();
                                    ((IdentifySoundModel) taskModel).setRightAnswer(task.getField("rightAnswer").toString());
                                    ((IdentifySoundModel) taskModel).setOption2(task.getField("option2").toString());
                                    ((IdentifySoundModel) taskModel).setOption3(task.getField("option3").toString());
                                    ((IdentifySoundModel) taskModel).setOption4(task.getField("option4").toString());

                                    CDAAsset audioAsset = (CDAAsset) task.getField("audio");
                                    ((IdentifySoundModel) taskModel).setAudio("https:" + audioAsset.url());

                                    taskValue = 2;

                                } else if (contentType.equals("eLettersTask")) {
                                    taskModel = new LettersModel();
                                    ((LettersModel) taskModel).setSolution(task.getField("solution").toString());
                                    int lettersLeft = totalAmountOfLetters - task.getField("solution").toString().length();
                                    String otherLetters = "";

                                    for (int i = 0; i < lettersLeft; i++) {
                                        Random r = new Random();
                                        char c = (char) (r.nextInt(26) + 'a');
                                        otherLetters = otherLetters + c;
                                    }

                                    ((LettersModel) taskModel).setOtherLetters(otherLetters);

                                    taskValue = 3;

                                } else if (contentType.equals("eGuessTheImageTask")) {
                                    taskModel = new PictureCountdownModel();
                                    ((PictureCountdownModel) taskModel).setRightAnswer(task.getField("rightAnswer").toString());
                                    ((PictureCountdownModel) taskModel).setOption2(task.getField("option2").toString());
                                    ((PictureCountdownModel) taskModel).setOption3(task.getField("option3").toString());

                                    taskValue = 4;
                                } else if (contentType.equals("eTrueFalseTask")) {
                                    taskModel = new TrueFalseModel();
                                    ((TrueFalseModel) taskModel).setIsTrue(Boolean.parseBoolean(task.getField("isTrue").toString()));


                                    taskValue = 5;
                                }

                                taskModel.setContentfulID(task.id());
                                taskModel.setTourContentfulID(selectedTour);

                                CDAEntry station = (CDAEntry) task.getField("stationId");
                                taskModel.setStationContentfulID(station.id());

                                taskModel.setScore(Double.valueOf(task.getField("score").toString()).intValue());
                                taskModel.setQuestion(task.getField("question").toString());
                                taskModel.setAnswerCorrect(task.getField("answerCorrect").toString());
                                taskModel.setAnswerWrong(task.getField("answerWrong").toString());

                                if (task.getField("picture") != null) {
                                    CDAAsset pictureAsset = (CDAAsset) task.getField("picture");
                                    taskModel.setPicturePath("https:" + pictureAsset.url());

                                    new DownloadImage(activity).execute(pictureAsset.url(), selectedTour, "taskId", task.id(), "picture");
                                } else {
                                    taskModel.setPicturePath("");
                                }

                                if (task.getField("answerPicture") != null) {
                                    CDAAsset answerPictureAsset = (CDAAsset) task.getField("answerPicture");
                                    taskModel.setAnswerPicture("https:" + answerPictureAsset.url());

                                    new DownloadImage(activity).execute(answerPictureAsset.url(), selectedTour, "taskId", task.id(), "answerPicture");
                                } else {
                                    taskModel.setAnswerPicture("");
                                }

                                switch (taskValue) {
                                    case 0:
                                        quizModels.add((QuizModel)taskModel);
                                        break;
                                    case 1:
                                        sliderModels.add((SliderModel) taskModel);
                                        break;
                                    case 2:
                                        identifySoundModels.add((IdentifySoundModel) taskModel);
                                        break;
                                    case 3:
                                        lettersModels.add((LettersModel) taskModel);
                                        break;
                                    case 4:
                                        pictureCountdownModels.add((PictureCountdownModel) taskModel);
                                        break;
                                    case 5:
                                        trueFalseModels.add((TrueFalseModel) taskModel);
                                        break;

                                }


                            } else if (entry.getField("infoPopupId") != null) {
                                DoUKnowModel infoPopupModel = new DoUKnowModel();
                                CDAEntry infoPopup = (CDAEntry) entry.getField("infoPopupId");
                                chronologyModel.setInfoPopupContentfulID(infoPopup.id());

                                infoPopupModel.setTourContentfulID(selectedTour);

                                infoPopupModel.setContentfulID(infoPopup.id());

                                infoPopupModel.setContentText(infoPopup.getField("contentText").toString());

                                if (infoPopup.getField("picture") != null) {
                                    CDAAsset pictureAsset = (CDAAsset) infoPopup.getField("picture");
                                    infoPopupModel.setPicturePath("https:" + pictureAsset.url());


                                    new DownloadImage(activity).execute(pictureAsset.url(), selectedTour, "infoPopupId", infoPopup.id(), "picture");
                                }


                                if (infoPopup.getField("location") != null) {

                                    String location = infoPopup.getField("location").toString();
                                    String parts[] = location.split(",");
                                    double latitude = Double.valueOf(parts[0].substring(5));
                                    double longitude = Double.valueOf(parts[1].substring(5, parts[1].length() - 1));
                                    infoPopupModel.setLatitude(latitude);
                                    infoPopupModel.setLongitude(longitude);
                                }

                                infoPopups.add(infoPopupModel);

                            }
                            chronologyModels.add(chronologyModel);

                        }


                        String root = "/data/data/hsaugsburg.zirbl001/";

                        storeDataInStorage(root + "chronology" + selectedTour + ".json", chronologyModels);
                        storeDataInStorage(root + "infoPopups" + selectedTour + ".json", infoPopups);
                        storeDataInStorage(root + "stations" + selectedTour + ".json", stations);
                        storeDataInStorage(root + "identifySoundTasks" + selectedTour + ".json", identifySoundModels);
                        storeDataInStorage(root + "lettersTasks" + selectedTour + ".json", lettersModels);
                        storeDataInStorage(root + "pictureCountdownTasks" + selectedTour + ".json", pictureCountdownModels);
                        storeDataInStorage(root + "quizTasks" + selectedTour + ".json", quizModels);
                        storeDataInStorage(root + "sliderTasks" + selectedTour + ".json", sliderModels);
                        storeDataInStorage(root + "trueFalseTasks" + selectedTour + ".json", trueFalseModels);


                        downloadActivity.downloadFinished();
                    }



                    @Override
                    public void onError(Throwable error) {
                        Log.e("Contentful", "could not request entry", error);
                    }

                    @Override
                    public void onNext(CDAArray cdaArray) {
                        result = cdaArray;
                    }
                });
    }

    private void storeDataInStorage(String location, List<?> list) {
        try {
            Gson gson = new Gson();
            FileWriter fileWriter = new FileWriter(location);
            fileWriter.write("[");
            for (int i = 0; i < list.size(); i++) {

                String json = gson.toJson(list.get(i));
                fileWriter.write(json);

                if (i != list.size() - 1) {
                    fileWriter.write(",");
                }
            }

            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
