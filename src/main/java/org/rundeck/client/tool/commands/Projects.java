package org.rundeck.client.tool.commands;

import org.rundeck.client.api.RundeckApi;
import org.rundeck.client.api.model.ProjectItem;
import org.rundeck.client.belt.Command;
import org.rundeck.client.belt.CommandOutput;
import org.rundeck.client.belt.CommandRunFailure;
import org.rundeck.client.tool.App;
import org.rundeck.client.tool.options.ProjectCreateOptions;
import org.rundeck.client.tool.options.ProjectOptions;
import org.rundeck.client.util.Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by greg on 5/19/16.
 */
@Command(description = "List and manage projects.")
public class Projects extends ApiCommand {
    public Projects(final Client<RundeckApi> client) {
        super(client);
    }

    @Command(isDefault = true)
    public void list(CommandOutput output) throws IOException {
        List<ProjectItem> body = client.checkError(client.getService().listProjects());
        output.output(String.format("%d Projects:%n", body.size()));
        for (ProjectItem proj : body) {
            output.output("* " + proj.toBasicString());
        }
    }

    @Command(description = "Delete a project")
    public void delete(ProjectOptions projectOptions, CommandOutput output) throws IOException {
        client.checkError(client.getService().deleteProject(projectOptions.getProject()));
        output.output(String.format("Project was deleted: %s%n", projectOptions.getProject()));
    }

    @Command(description = "Create a project.")
    public void create(ProjectCreateOptions options, CommandOutput output) throws IOException {
        Map<String, String> config = new HashMap<>();
        if (options.config().size() > 0) {
            for (String s : options.config()) {
                if (!s.startsWith("--")) {
                    throw new IllegalArgumentException("Expected --key=value, but saw: " + s);
                }
                s = s.substring(2);
                String[] arr = s.split("=", 2);
                if (arr.length != 2) {
                    throw new IllegalArgumentException("Expected --key=value, but saw: " + s);
                }
                config.put(arr[0], arr[1]);
            }
        }
        ProjectItem project = new ProjectItem();
        project.setName(options.getProject());
        project.setConfig(config);

        ProjectItem body = client.checkError(client.getService().createProject(project));
        output.output(String.format("Created project: \n\t%s%n", body.toBasicString()));
    }
}