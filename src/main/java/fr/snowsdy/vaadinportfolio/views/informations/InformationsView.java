package fr.snowsdy.vaadinportfolio.views.informations;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import elemental.json.Json;
import fr.snowsdy.vaadinportfolio.data.entity.Info;
import fr.snowsdy.vaadinportfolio.data.service.InfoService;
import fr.snowsdy.vaadinportfolio.views.MainLayout;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;

@PageTitle("Informations")
@Route(value = "informations/:infoID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class InformationsView extends Div implements BeforeEnterObserver {

    private final String INFO_ID = "infoID";
    private final String INFO_EDIT_ROUTE_TEMPLATE = "informations/%s/edit";

    private Grid<Info> grid = new Grid<>(Info.class, false);

    private TextField title;
    private Upload imagePath;
    private Image imagePathPreview;
    private DatePicker addedAt;
    private TextField description;
    private TextField githubLink;
    private TextField language;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Info> binder;

    private Info info;

    private final InfoService infoService;

    @Autowired
    public InformationsView(InfoService infoService) {
        this.infoService = infoService;
        addClassNames("informations-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("title").setAutoWidth(true);
        LitRenderer<Info> imagePathRenderer = LitRenderer.<Info>of("<img style='height: 64px' src=${item.imagePath} />")
                .withProperty("imagePath", Info::getImagePath);
        grid.addColumn(imagePathRenderer).setHeader("Image Path").setWidth("68px").setFlexGrow(0);

        grid.addColumn("addedAt").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("githubLink").setAutoWidth(true);
        grid.addColumn("language").setAutoWidth(true);
        grid.setItems(query -> infoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(INFO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(InformationsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Info.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(imagePath, imagePathPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.info == null) {
                    this.info = new Info();
                }
                binder.writeBean(this.info);
                this.info.setImagePath(imagePathPreview.getSrc());

                infoService.update(this.info);
                clearForm();
                refreshGrid();
                Notification.show("Info details stored.");
                UI.getCurrent().navigate(InformationsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the info details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> infoId = event.getRouteParameters().get(INFO_ID).map(UUID::fromString);
        if (infoId.isPresent()) {
            Optional<Info> infoFromBackend = infoService.get(infoId.get());
            if (infoFromBackend.isPresent()) {
                populateForm(infoFromBackend.get());
            } else {
                Notification.show(String.format("The requested info was not found, ID = %s", infoId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(InformationsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        title = new TextField("Title");
        Label imagePathLabel = new Label("Image Path");
        imagePathPreview = new Image();
        imagePathPreview.setWidth("100%");
        imagePath = new Upload();
        imagePath.getStyle().set("box-sizing", "border-box");
        imagePath.getElement().appendChild(imagePathPreview.getElement());
        addedAt = new DatePicker("Added At");
        description = new TextField("Description");
        githubLink = new TextField("Github Link");
        language = new TextField("Language");
        Component[] fields = new Component[]{title, imagePathLabel, imagePath, addedAt, description, githubLink,
                language};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Info value) {
        this.info = value;
        binder.readBean(this.info);
        this.imagePathPreview.setVisible(value != null);
        if (value == null) {
            this.imagePathPreview.setSrc("");
        } else {
            this.imagePathPreview.setSrc(value.getImagePath());
        }

    }
}
