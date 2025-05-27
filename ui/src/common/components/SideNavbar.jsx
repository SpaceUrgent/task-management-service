import React from "react";
import {Link, useLocation} from "react-router-dom";

export default function SideNavbar({ onSignOut }) {
    const location = useLocation();

    function getFirstUriPathPart() {
        return location.pathname.split("/")[1];
    }

    const isActive = (path) => getFirstUriPathPart() === path;

    return (
       <nav className="col-md-2 d-md-block sidebar sticky-top vh-100 bg-dark p-3 text-white w-20">
           <div className="sidebar-sticky h-100 d-flex flex-column">
               <span className="navbar-brand fs-4 ms-3">PM Application</span>
               <hr/>
               <ul className="nav nav-pills flex-column mb-auto">
                   <li className="nav-item mb-1">
                       <Link
                           className={`nav-link ${isActive("projects") || !getFirstUriPathPart() ? "active" : "text-light"}`}
                           to="/projects"
                       >
                           Projects
                       </Link>
                   </li>
                   <li className="nav-item mb-1">
                       <Link
                           className="nav-link text-light"
                           to="/projects"
                       >
                           Tasks (In development)
                       </Link>
                   </li>
                   <li className="nav-item mb-1">
                       <Link
                           className={`nav-link ${isActive("profile") ? "active" : "text-light"}`}
                           to="/profile"
                       >
                           Profile
                       </Link>
                   </li>
               </ul>
               <hr className='m-1'/>
               <button type="button" className="btn btn-outline-danger m-2" onClick={onSignOut}>
                   Sign Out
               </button>
           </div>
       </nav>
    )
}