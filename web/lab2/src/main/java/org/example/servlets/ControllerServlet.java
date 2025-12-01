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
import java.util.List;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String x = req.getParameter("x");
        String y = req.getParameter("y");
        String r = req.getParameter("r");

        if (x != null && y != null && r != null && !x.isEmpty() && !y.isEmpty() && !r.isEmpty()) {
            req.getRequestDispatcher("/area").forward(req, resp);
        } else {
            HttpSession session = req.getSession();
            List<Result> results = (List<Result>) session.getAttribute("results");
            if (results == null) {
                results = new ArrayList<>();
                session.setAttribute("results", results);
            }
            req.setAttribute("results", results);

            boolean suppressPrefill = "true".equals(req.getParameter("fromCanvas"));

            Object last = session.getAttribute("lastResult");
            if (last instanceof Result lastResult) {
                if (!suppressPrefill) {
                    req.setAttribute("x", lastResult.getX().stripTrailingZeros().toPlainString());
                    req.setAttribute("y", lastResult.getY().stripTrailingZeros().toPlainString());
                }
                req.setAttribute("r", lastResult.getR().stripTrailingZeros().toPlainString());
            }
            req.getRequestDispatcher("/form.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
