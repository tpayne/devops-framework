/**
 * Pipeline plugin extension for tagging containers
 */
package org.devops.framework.plugin;

import org.devops.framework.core.ConfigPropertiesConstants;
import org.devops.framework.core.Container;

import java.io.File;

import hudson.Extension;
import hudson.Util;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

import com.google.common.collect.ImmutableSet;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;

import javax.inject.Inject;

import jenkins.MasterToSlaveFileCallable;
import jenkins.util.BuildListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Set;

public class DevOpsFrameworkTagContainerStepExecution extends SynchronousNonBlockingStepExecution<Boolean> {

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient Run build;

    @StepContextParameter
    private transient Launcher launcher;

    private transient final DevOpsFrameworkTagContainerStep step;

    /**
     * Default constructor
     * 
     * @param DevOpsFrameworkTagContainerStep - step
     * @param StepContext - context
     */
    DevOpsFrameworkTagContainerStepExecution(DevOpsFrameworkTagContainerStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    /**
     * Run function
     * 
     * @return Boolean
     * @throws Exception
     */
    @Override
    protected Boolean run() throws Exception {
        listener = getContext().get(TaskListener.class);
        StringBuffer outputStr = new StringBuffer();
        listener.getLogger().println("Tagging container "+step.getContainerName());
        boolean retStat = Container.tagContainer(ConfigPropertiesConstants.DOCKER,
                                                 step.getContainerName(),
                                                 step.getTargetName(),
                                                 outputStr);
        String output = outputStr.toString();
        if (retStat) {
            listener.getLogger().println(output);
        } else {
            listener.error(output);
        }
        outputStr = null;
        return retStat;
    }

    private static final long serialVersionUID = 1L;
}

