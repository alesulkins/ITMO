package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.Result;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/clear")
public class ClearHistoryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        session.setAttribute("results", new ArrayList<Result>());
        session.removeAttribute("lastResult");

        req.setAttribute("results", new ArrayList<Result>());
        req.getRequestDispatcher("/form.jsp").forward(req, resp);
    }
}