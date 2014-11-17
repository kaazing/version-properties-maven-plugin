/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.maven.plugins;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Store Kaazing version properties into the build
 *
 * @goal version
 *
 * @phase initialize
 */
public class VersionsMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @since 1.0
     */
    private MavenProject project;
    
    /**
     * @parameter expression="${release.level}"
     * @readonly
     * @since 3.2
     */
    private String releaseLevel;

    public void execute() throws MojoExecutionException {
        String projectVersion = project.getVersion();
        getLog().info("project.version = " + projectVersion);

        String[] versionComponents = projectVersion.split("\\.");
        if (versionComponents.length < 4)
          throw new MojoExecutionException("Project version must consist of major.minor.patch.build");

        Properties props = project.getProperties();
        
        String majorVersion = versionComponents[0];
        props.put("project.version.major", majorVersion);
        getLog().info("project.version.major = " + majorVersion);

        String minorVersion = majorVersion + "." + versionComponents[1];
        props.put("project.version.minor", minorVersion);
        getLog().info("project.version.minor = " + minorVersion);

        // For Windows service-related stuff, we need a version of project.version.minor
        // without the included dots.
        String minorCompactVersion = majorVersion + versionComponents[1];
        props.put("project.version.minor.compact", minorCompactVersion);
        getLog().info("project.version.minor.compact = " + minorCompactVersion);

        String patchVersion = minorVersion + "." + versionComponents[2];
        props.put("project.version.patch", patchVersion);
        getLog().info("project.version.patch = " + patchVersion);
                
	// During development, the build version may have -SNAPSHOT on the
	// end of it, which things like Windows installers don't like.
	// We're removing that here.
	String buildVal = versionComponents[3];
	int index = buildVal.indexOf("-SNAPSHOT");
	if (index >= 0) {
	    buildVal = buildVal.substring(0, index);
	}
        String buildVersion = patchVersion + "." + buildVal;
        props.put("project.version.build", buildVersion);
        getLog().info("project.version.build = " + buildVersion);
                
        // Define what we'll use for the Implementation-Version.  We want
        // ${pom.version} and (if we're at beta) tack on ' Beta'.  We do
        // not want anything (like ' Production') if we're at something
        // other than beta.
        String implementationVersion = projectVersion;
        if ("beta".equals(releaseLevel)) {
            implementationVersion += " Beta";
        }
        props.put("project.version.implementation", implementationVersion);
        getLog().info("project.version.implementation = " + implementationVersion);
    }
}
