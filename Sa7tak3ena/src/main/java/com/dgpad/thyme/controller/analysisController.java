//import com.dgpad.thyme.repository.AmbulanceRequestRepository;
//import com.dgpad.thyme.repository.RequestRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//// AnalysisController.java
//@Controller
//@RequestMapping("/analysis")
//public class analysisController {
//
//    @Autowired
//    private userRepository userRepository;
//
//    @Autowired
//    private AmbulanceRequestRepository ambulanceRequestRepository;
//
//    @Autowired
//    private RequestRepository hospitalRequestRepository;
//
//    @GetMapping("/userRoleChart")
//    public String getUserRoleChart(Model model) {
//        // Create dataset for user roles
//        CategoryDataset userRoleDataset = createUserRoleDataset();
//
//        // Create chart for user roles
//        JFreeChart userRoleChart = ChartFactory.createBarChart(
//                "User Roles Analysis",
//                "Roles",
//                "Number of Users",
//                userRoleDataset
//        );
//
//        // Save the user role chart as a PNG file
//        saveChartAsPNG(userRoleChart, "userRoleAnalysis.png");
//
//        // Pass the user role chart file name to the view
//        model.addAttribute("userRoleChartFileName", "userRoleAnalysis.png");
//
//        // Create dataset for requests
//        DefaultCategoryDataset requestDataset = new DefaultCategoryDataset();
//        requestDataset.addValue(ambulanceRequestRepository.count(), "Requests", "Ambulance");
//        requestDataset.addValue(hospitalRequestRepository.count(), "Requests", "Hospital");
//
//        // Create chart for requests
//        JFreeChart requestChart = ChartFactory.createBarChart(
//                "Request Analysis",
//                "Request Types",
//                "Number of Requests",
//                requestDataset
//        );
//
//        // Save the request chart as a PNG file
//        saveChartAsPNG(requestChart, "requestAnalysis.png");
//
//        // Pass the request chart file name to the view
//        model.addAttribute("requestChartFileName", "requestAnalysis.png");
//
//        return "analysis/userRoleAndRequestChart";
//    }
//
//    private CategoryDataset createUserRoleDataset() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(userRepository.countByRole("ADMIN"), "Roles", "Admin");
//        dataset.addValue(userRepository.countByRole("USER"), "Roles", "User");
//        // add other roles as needed
//        return dataset;
//    }
//
//    private void saveChartAsPNG(JFreeChart chart, String fileName) {
//        try {
//            ChartUtils.saveChartAsPNG(new File(fileName), chart, 600, 400);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

