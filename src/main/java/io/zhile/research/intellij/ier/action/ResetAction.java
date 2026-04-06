package io.zhile.research.intellij.ier.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import io.zhile.research.intellij.ier.helper.*;
import io.zhile.research.intellij.ier.listener.AppActivationListener;
import io.zhile.research.intellij.ier.listener.AppEventListener;
import io.zhile.research.intellij.ier.listener.BrokenPluginsListener;
import io.zhile.research.intellij.ier.tw.MainToolWindowFactory;
import io.zhile.research.intellij.ier.ui.dialog.MainDialog;
import org.jetbrains.annotations.NotNull;

public class ResetAction extends AnAction implements DumbAware {
    static {
        CustomProperties.fix();
        BrokenPlugins.fix();
        BrokenPluginsListener.getInstance().listen();

        AppEventListener.getInstance().listen();
        AppActivationListener.getInstance().listen();
        CustomRepository.checkAndAdd(CustomRepository.DEFAULT_HOST);
    }

    public ResetAction() {
        super(Constants.ACTION_NAME, "Reset my IDE eval information", AllIcons.General.Reset);

        AnAction optionsGroup = ActionManager.getInstance().getAction("WelcomeScreen.Options");
        if ((optionsGroup instanceof DefaultActionGroup)) {
            ((DefaultActionGroup) optionsGroup).add(this);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        NotificationHelper.checkAndExpire(e);

        if (project == null) {
            MainDialog mainDialog = new MainDialog(Constants.ACTION_NAME);
            mainDialog.show();

            return;
        }

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.ACTION_NAME);

        if (toolWindow == null) {
            // Register the tool window with new API
            toolWindow = ToolWindowManager.getInstance(project)
                    .registerToolWindow(Constants.ACTION_NAME, true, ToolWindowAnchor.BOTTOM);

            // Set icon
            toolWindow.setIcon(IconLoader.getIcon("/icons/reset.png")); // replace with your icon path

            // Set content using your factory
            MainToolWindowFactory factory = new MainToolWindowFactory();
            factory.createToolWindowContent(project, toolWindow);
        }

        // Show the tool window
        toolWindow.show(null);
    }
}
