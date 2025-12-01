package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.Result;
import org.example.model.AreaChecker;
import org.example.model.Params;
import org.example.model.ValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/area")
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            boolean fromCanvas = "true".equals(req.getParameter("fromCanvas"));
            Params params = Params.fromRequest(req, fromCanvas);
            var x = params.getX();
            var y = params.getY();
            var r = params.getR();

            boolean hit = AreaChecker.isInside(x, y, r);

            Result result = new Result(x, y, r, hit, LocalDateTime.now());

            HttpSession session = req.getSession();
            List<Result> results = (List<Result>) session.getAttribute("results");
            if (results == null) {
                results = new ArrayList<>();
                session.setAttribute("results", results);
            }
            results.add(0, result);
            if (results.size() > 100) results.remove(results.size() - 1);

            session.setAttribute("lastResult", result);

            if (fromCanvas) {
                resp.sendRedirect(req.getContextPath() + "/controller?fromCanvas=true");
            } else {
                resp.sendRedirect(req.getContextPath() + "/result");
            }

        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/form.jsp").forward(req, resp);
        }
    }
}
