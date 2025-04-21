import React from "react";
import {Link} from "react-router-dom";

export default function SideNavbar({ onSignOut }) {

    return (
       <nav className="col-md-2 d-md-block sidebar bg-dark p-3 text-white w-25">
           <div className="sidebar-sticky h-100">
               <span className="navbar-brand fs-4 ms-3">PM Application</span>
               <hr/>
               <ul className="nav nav-pills flex-column mb-auto">
                   <li className="nav-item mb-1">
                       <Link className="nav-link active" to="/projects">Projects</Link>
                   </li>
                   <li className="nav-item mb-1">
                       <Link className="nav-link text-light" to="/projects">Tasks (In development)</Link>
                   </li>
                   <li className="nav-item mb-1">
                       <Link className="nav-link text-light" to="/projects">Profile (In development)</Link>
                   </li>
               </ul>
               <hr className='m-1'/>
               <button type="button" className="btn btn-outline-secondary m-2" onClick={onSignOut}>
                   <a>Sign Out </a>
                   <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                        className="bi bi-box-arrow-right" viewBox="0 0 20 20">
                       <path fill-rule="evenodd"
                             d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0z"/>
                       <path fill-rule="evenodd"
                             d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708z"/>
                   </svg>
               </button>
           </div>
       </nav>
    )
}