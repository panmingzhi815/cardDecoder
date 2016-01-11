package sample;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang.StringUtils;
import org.pan.transport.MessageAddress;
import org.pan.transport.MessageTransport;
import org.pan.transport.MessageTransportFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Controller implements Initializable {

    @FXML
    public TableView<CardInfo> TableView_cardList;
    @FXML
    public TextArea TextArea_log;
    final Image imageStart = new Image(getClass().getResourceAsStream("key_start_72.png"));
    final Image imageStop = new Image(getClass().getResourceAsStream("key_stop_72.png"));
    final byte[] cmd0 = ByteUtils.hexStringToByteArray("AA 01 22 00 FF FF FF FF FF FF 44 4C 47 58 48 54 82 BB");
    final byte[] cmd1 = ByteUtils.hexStringToByteArray("AA 01 22 01 FF FF FF FF FF FF 44 4C 47 58 48 54 83 BB");
    final byte[] cmd2 = ByteUtils.hexStringToByteArray("AA 01 22 02 FF FF FF FF FF FF 44 4C 47 58 48 54 80 BB");
    final byte[] cmd3 = ByteUtils.hexStringToByteArray("AA 01 22 03 FF FF FF FF FF FF 44 4C 47 58 48 54 81 BB");
    final byte[] readCard = ByteUtils.hexStringToByteArray("AA 01 25 8E BB");
    final byte[] loadOldPassword = ByteUtils.hexStringToByteArray("AA 01 26 00 FF FF FF FF FF FF 8D BB");


    final byte[] cmd0_2 = ByteUtils.hexStringToByteArray("AA 01 22 00 44 4C 47 58 48 54 FF FF FF FF FF FF 82 BB");
    final byte[] cmd1_2 = ByteUtils.hexStringToByteArray("AA 01 22 01 44 4C 47 58 48 54 FF FF FF FF FF FF 83 BB");
    final byte[] cmd2_2 = ByteUtils.hexStringToByteArray("AA 01 22 02 44 4C 47 58 48 54 FF FF FF FF FF FF 80 BB");
    final byte[] cmd3_2 = ByteUtils.hexStringToByteArray("AA 01 22 03 44 4C 47 58 48 54 FF FF FF FF FF FF 81 BB");
    final byte[] loadOldPassword2 = ByteUtils.hexStringToByteArray("AA 01 26 00 44 4C 47 58 48 54 86 BB");
    public Button Button_start;

    @FXML public RadioMenuItem COM1;
    @FXML public RadioMenuItem COM2;
    @FXML public RadioMenuItem COM3;
    @FXML public RadioMenuItem COM4;
    @FXML public RadioMenuItem COM5;
    @FXML public RadioMenuItem COM6;

    private ToggleGroup toggleGroup = new ToggleGroup();

    private ScheduledExecutorService scheduledExecutorService;
    private String COM;

    private AtomicLong atomicLong = new AtomicLong(0);
    private boolean reset = false;
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        TextArea_log.appendText("在这里将显示加密操作信息：\n");
        TextArea_log.appendText("提示：请先选择串口，再单击钥匙图标开始\n");

        ObservableList<TableColumn<CardInfo,?>> observableList = TableView_cardList.getColumns();
        observableList.get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        observableList.get(1).setCellValueFactory(new PropertyValueFactory<>("cardId"));
        observableList.get(2).setCellValueFactory(new PropertyValueFactory<>("size"));

        Button_start.setGraphic(new ImageView(imageStop));
        COM1.setToggleGroup(toggleGroup);
        COM2.setToggleGroup(toggleGroup);
        COM3.setToggleGroup(toggleGroup);
        COM4.setToggleGroup(toggleGroup);
        COM5.setToggleGroup(toggleGroup);
    }

    @FXML
    public void changeCOM1_onAction() {
        COM = "COM1";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM1\n"));
    }

    @FXML
    public void changeCOM2_onAction() {
        COM = "COM2";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM2\n"));
    }

    @FXML
    public void changeCOM3_onAction() {
        COM = "COM3";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM3\n"));
    }

    @FXML
    public void changeCOM4_onAction() {
        COM = "COM4";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM4\n"));
    }

    @FXML
    public void changeCOM5_onAction() {
        COM = "COM5";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM5\n"));
    }

    @FXML
    public void changeCOM6_onAction() {
        COM = "COM6";
        Platform.runLater(() -> TextArea_log.appendText("当前串口：COM6\n"));
    }

    @FXML
    public void start_onAction() {
        if (StringUtils.isBlank(COM)){
            new Alert(Alert.AlertType.INFORMATION,"请先选择一个COM口", ButtonType.OK).show();
            return;
        }
        if (scheduledExecutorService == null){
            Button_start.setGraphic(new ImageView(imageStart));
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                byte[] bytes;
                String cardId;
                try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                    if (reset){
                        messageTransport.sendMessage(loadOldPassword2, 6, 200,50);
                    }else {
                        messageTransport.sendMessage(loadOldPassword, 6, 200,50);
                    }

                    bytes = messageTransport.sendMessage(readCard, 9, 200,50);
                    if (bytes == null){
                        return;
                    }
                    cardId = ByteUtils.byteArrayToHexStringNoFormat(Arrays.copyOfRange(bytes, 3, 7));
                    if(TableView_cardList.getItems().contains(new CardInfo("0",cardId,"0"))){
                        Platform.runLater(()-> TextArea_log.appendText("该卡已经加密 :" +cardId+ "\n"));
                        return;
                    }
                } catch (Exception e) {
                    return;
                }

                final AtomicLong size = new AtomicLong(0);
                if (reset){
                    Platform.runLater(()-> TextArea_log.appendText("加密开始\n"));
                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd0_2, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第0扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第0扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd1_2, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第1扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第1扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd2_2, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第2扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第2扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd3_2, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第3扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第3扇区失败\n"));
                    }

                    Platform.runLater(()-> {
                        long andAdd = atomicLong.getAndAdd(1);
                        TableView_cardList.getItems().add(0,new CardInfo(String.valueOf(andAdd), cardId,String.valueOf(size.get())));
                        TextArea_log.appendText("加密结束,请换卡\n");
                    });
                }else {
                    Platform.runLater(()-> TextArea_log.appendText("加密开始\n"));
                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd0, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第0扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第0扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd1, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第1扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第1扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd2, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第2扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第2扇区失败\n"));
                    }

                    try (MessageTransport messageTransport = MessageTransportFactory.createMessageTransport(MessageAddress.createComMessageAddress(COM))){
                        messageTransport.sendMessage(cmd3, 9, 200,100);
                        size.addAndGet(1);
                        Platform.runLater(() -> TextArea_log.appendText("加密第3扇区成功\n"));
                    } catch (Exception e) {
                        Platform.runLater(()-> TextArea_log.appendText("加密第3扇区失败\n"));
                    }

                    Platform.runLater(()-> {
                        long andAdd = atomicLong.getAndAdd(1);
                        TableView_cardList.getItems().add(0,new CardInfo(String.valueOf(andAdd), cardId,String.valueOf(size.get())));
                        TextArea_log.appendText("加密结束,请换卡\n");
                    });
                }
            }, 1000, 100, TimeUnit.MILLISECONDS);
        }else {
            scheduledExecutorService.shutdown();
            Button_start.setGraphic(new ImageView(imageStop));
            scheduledExecutorService = null;
        }
    }

    public void clean_onAction() {
        TableView_cardList.getItems().clear();
        atomicLong.set(0);
    }

    public void reset_onAction() {
        reset = true;
        TableView_cardList.getItems().clear();
        atomicLong.set(0);

        StageHelper.getStages().get(0).setTitle(Main.title_version + ":正在还原初始密码");
    }

    public void unReset_onAction() {
        reset = false;
        TableView_cardList.getItems().clear();
        atomicLong.set(0);

        StageHelper.getStages().get(0).setTitle(Main.title_version + ":正在加密卡片");
    }
}
