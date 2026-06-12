package hr.from.juricamigac.mockclean.action;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import hr.from.juricamigac.mockclean.core.JsonCleanResult;
import hr.from.juricamigac.mockclean.core.JsonCleaner;
import hr.from.juricamigac.mockclean.core.JsonCleanerException;
import hr.from.juricamigac.mockclean.settings.AemJsonCleanerSettings;

public final class CleanCurrentJsonAction extends AnAction {

    private static final String NOTIFICATION_GROUP = "MockClean AEM";

    @Override
    public void actionPerformed(final AnActionEvent event) {
        final Project project = event.getProject();
        final Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            notify(project, "MockClean AEM", "Open a JSON file in the editor before running the cleaner.", NotificationType.WARNING);
            return;
        }

        final Document document = editor.getDocument();
        VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null) {
            virtualFile = FileDocumentManager.getInstance()
                                             .getFile(document);
        }

        if (virtualFile == null || !isJsonFile(virtualFile)) {
            notify(project, "MockClean AEM", "The current editor file is not a JSON file.", NotificationType.WARNING);
            return;
        }

        if (!virtualFile.isWritable()) {
            notify(project, "MockClean AEM", "The current JSON file is read-only.", NotificationType.WARNING);
            return;
        }

        final Set<String> excludedVariables = AemJsonCleanerSettings.getInstance()
                                                                    .getExcludedVariableSet();
        final JsonCleanResult cleanResult;
        try {
            cleanResult = JsonCleaner.clean(document.getText(), excludedVariables);
        } catch (final JsonCleanerException exception) {
            notify(project, "MockClean AEM", "Invalid JSON. Nothing was changed. " + exception.getMessage(), NotificationType.ERROR);
            return;
        }

        if (cleanResult.getRemovedCount() == 0) {
            notify(project, "MockClean AEM", "No configured AEM metadata properties were found.", NotificationType.INFORMATION);
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(0, document.getTextLength(), cleanResult.getJson());
            FileDocumentManager.getInstance()
                               .saveDocument(document);
        });

        notify(project, "MockClean AEM", "Removed " + cleanResult.getRemovedCount() + " JSON propert" + (cleanResult.getRemovedCount() == 1 ? "y" : "ies") + ".", NotificationType.INFORMATION);
    }

    @Override
    public void update(final AnActionEvent event) {
        final Project project = event.getProject();
        final Editor editor = event.getData(CommonDataKeys.EDITOR);

        event.getPresentation()
             .setEnabledAndVisible(project != null && editor != null);
    }

    private static boolean isJsonFile(final VirtualFile virtualFile) {
        return Optional.of(virtualFile)
                       .map(VirtualFile::getExtension)
                       .filter(StringUtils::isNotEmpty)
                       .filter(extension -> StringUtils.equalsIgnoreCase(extension, "json"))
                       .isPresent();
    }

    private static void notify(final Project project, final String title, final String content, final NotificationType type) {
        NotificationGroupManager.getInstance()
                                .getNotificationGroup(NOTIFICATION_GROUP)
                                .createNotification(title, content, type)
                                .notify(project);
    }

}
