import React from "react";
import {ProjectContext, ProjectContextProvider} from "../contexts/ProjectContext";
import {Outlet} from "react-router-dom";

export default function ProjectContextLayout() {
    return(
        <ProjectContextProvider>
            <Outlet/>
        </ProjectContextProvider>
    )
}