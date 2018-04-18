package baobab.notebookfx.controls;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.CheckFile;
import baobab.notebookfx.utils.SpringFX;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;

public class ContentItemPane {

    private static final String DATE_FORMAT = "dd/M/yyyy";

    private static final ContentManager contentManager = SpringFX.getInstance()
            .getApplicationContex()
            .getBean(ContentManager.class);

    private static final Pattern PATTERN_TEXT_PREVIEW = Pattern.compile("(?m)^>([\\s\\S]+?)$");

    private static String imageResource = ContentItemPane.class.getResource("/img/label_16.png").toExternalForm();

    private static javafx.scene.image.Image image = new javafx.scene.image.Image(imageResource);

    public static VBox content(Content content, IClick action) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        AnchorPane anchPane2 = new AnchorPane();
        VBox.setVgrow(anchPane2, Priority.ALWAYS);

        Hyperlink hyperL = new Hyperlink();
        hyperL.setText(content.getTitle());
//        hyperL.setId(Long.toString(content.getId()));// <= ID
        hyperL.setOnAction(actionEvent -> action.click("link", content.getId()));

        AnchorPane.setBottomAnchor(hyperL, 5.0);
        AnchorPane.setTopAnchor(hyperL, 5.0);
        AnchorPane.setLeftAnchor(hyperL, 5.0);
//        AnchorPane.setRightAnchor(hyperL, 205.0);
        AnchorPane.setRightAnchor(hyperL, 45.0);

        ////////////////////////////////////////////////////////////////////////
//        Label timeUpdate = new Label();
//        timeUpdate.getStyleClass().add("label-time-update");
//        timeUpdate.setText(content.getViewCount() + " "
//                + (new SimpleDateFormat(DATE_FORMAT)).format(content.getUpdateAt()));
//        AnchorPane.setTopAnchor(timeUpdate, 10.0);
//        AnchorPane.setRightAnchor(timeUpdate, 70.0);
//
//        Button editBtn = new Button();
//        editBtn.setId(Long.toString(content.getId()));
//        editBtn.getStyleClass().add("edit");
//
//        AnchorPane.setTopAnchor(editBtn, 5.0);
//        AnchorPane.setRightAnchor(editBtn, 40.0);
//
//        Button deleteBtn = new Button();
//        deleteBtn.setId(Long.toString(content.getId()));
//        deleteBtn.getStyleClass().add("delete");
//
//        AnchorPane.setTopAnchor(deleteBtn, 5.0);
//        AnchorPane.setRightAnchor(deleteBtn, 10.0);
        ////////////////////////////////////////////////////////////////////////
        MenuButton menuBtn = new MenuButton();
//        menuBtn.setId(Long.toString(content.getId()));
        menuBtn.getStyleClass().add("menu");

        MenuItem editMenuItem = new MenuItem("Edit");
//        editMenuItem.setId(Long.toString(content.getId()));
        editMenuItem.setGraphic(new ImageView(new javafx.scene.image.Image(
                ContentItemPane.class.getResource("/img/pencil_16.png")
                        .toExternalForm())));
        editMenuItem.setOnAction(actionEvent -> action.click("edit",content.getId()));
//        editMenuItem.getStyleClass().add("edit");

        MenuItem deleteMenuItem = new MenuItem("Delete");
//        deleteMenuItem.setId(Long.toString(content.getId()));
        deleteMenuItem.setGraphic(new ImageView(new javafx.scene.image.Image(
                ContentItemPane.class.getResource("/img/delete_16.png")
                        .toExternalForm())));
        deleteMenuItem.setOnAction(actionEvent -> action.click("delete", content.getId()));
//        deleteMenuItem.getStyleClass().add("delete");

        SeparatorMenuItem separatorMenu = new SeparatorMenuItem();

        MenuItem countMenuItem = new MenuItem(content.getViewCount() + "");
        countMenuItem.setGraphic(new ImageView(new javafx.scene.image.Image(
                ContentItemPane.class.getResource("/img/eye.png")
                        .toExternalForm())));
        countMenuItem.setDisable(true);

        MenuItem timeMenuItem = new MenuItem((new SimpleDateFormat(DATE_FORMAT)).format(content.getUpdateAt()));
        timeMenuItem.setGraphic(new ImageView(new javafx.scene.image.Image(
                ContentItemPane.class.getResource("/img/text-x-script_16.png")
                        .toExternalForm())));
        timeMenuItem.setDisable(true);

        menuBtn.getItems().addAll(editMenuItem, deleteMenuItem, separatorMenu, countMenuItem, timeMenuItem);

        AnchorPane.setTopAnchor(menuBtn, 5.0);
        AnchorPane.setRightAnchor(menuBtn, 5.0);
        ////////////////////////////////////////////////////////////////////////

        anchPane2.getChildren().addAll(hyperL, /*timeUpdate, editBtn, deleteBtn*/ menuBtn);

        HBox hBox2 = new HBox();
        hBox2.setPadding(new Insets(5));
        VBox.setVgrow(hBox2, Priority.ALWAYS);

        String imageId = CheckFile.getImageIdentifier(content.getContent());

        if (imageId != null) {
            Long id = Long.decode(imageId);
            Image image = contentManager.getImage(id);
            if (image != null) {
                try {
                    ImageView img = new ImageView();
                    img.setFitHeight(150.0);
                    img.setPickOnBounds(true);
                    img.setPreserveRatio(true);

                    ByteArrayInputStream in = new ByteArrayInputStream(image.getContent());
                    BufferedImage read = ImageIO.read(in);

                    img.setImage(SwingFXUtils.toFXImage(read, null));

                    hBox2.getChildren().add(img);
                } catch (IOException ex) {
                    Logger.getLogger(ContentItemPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        Matcher matcher = PATTERN_TEXT_PREVIEW.matcher(content.getContent());
        if (matcher.find()) {
            Label previewText = new Label();
            previewText.setAlignment(Pos.TOP_LEFT);
            previewText.setContentDisplay(ContentDisplay.TOP);
            previewText.setMaxHeight(Double.MAX_VALUE);
            previewText.setWrapText(true);

            HBox.setHgrow(previewText, Priority.ALWAYS);
            HBox.setMargin(previewText, new Insets(5.0));

            previewText.setText(matcher.group(1));

            hBox2.getChildren().add(previewText);
        }

        vBox.getChildren().addAll(anchPane2, hBox2);

        if (!content.getTags().isEmpty()) {
            FlowPane tilePane = new FlowPane();
            tilePane.setPadding(new Insets(5));
            tilePane.setHgap(5.0);
            tilePane.setVgap(5.0);

            content.getTags().stream().forEach(tag -> {
                HBox breadcrumb = new HBox();
                breadcrumb.getStyleClass().add("breadcrumb");

                do {
                    Label label = new Label(tag.getName());
                    label.setGraphic(new ImageView(image));
                    tag = tag.getParent();
                    breadcrumb.getChildren().add(0, label);
                    if (tag.getParent().getId() != 0) {
                        breadcrumb.getChildren().add(0, new Separator(Orientation.VERTICAL));
                    }
                } while (tag.getParent().getId() != 0);
                tilePane.getChildren().add(breadcrumb);
            });

            vBox.getChildren().addAll(tilePane);
            VBox.setVgrow(tilePane, Priority.ALWAYS);
        }

        vBox.getChildren().add(new Separator());

        return vBox;
    }

    public interface IClick {

        void click(String action, Long id);
    }

}
