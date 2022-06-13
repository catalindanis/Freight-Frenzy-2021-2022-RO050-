package org.firstinspires.ftc.teamcode.drive.autonomii_camera_albastru;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class DetectarePozitieAlbastru extends OpenCvPipeline {

    Telemetry telemetry;

    Mat mat = new Mat();
    public enum Location {
        UNU,
        DOI,
        TREI
    }

    private volatile Location location = Location.UNU;

    static final Rect LEFT_ROI = new Rect(
            new Point(70, 157),
            new Point(90, 179));
    static final Rect RIGHT_ROI = new Rect(
            new Point(165, 157),
            new Point(185, 179));
    static double PERCENT_COLOR_THRESHOLD = 0.4;

    //187 207


    public DetectarePozitieAlbastru(Telemetry t)
    {
telemetry=t;
    }

    @Override
    public Mat processFrame(Mat input) {


//170 230

        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        //Scalar lowHSV = new Scalar(168, 158, 154);
        //Scalar highHSV = new Scalar(179, 236, 255);

        Scalar lowHSV = new Scalar(173, 171, 21);
        Scalar highHSV = new Scalar(179, 255, 255);

        Core.inRange(mat, lowHSV, highHSV, mat);

        Mat left = mat.submat(LEFT_ROI);
        Mat right = mat.submat(RIGHT_ROI);



        double leftValue = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
        double rightValue = Core.sumElems(right).val[0] / RIGHT_ROI.area() / 255;

        left.release();
        right.release();




        telemetry.addData("Left raw value", (int) Core.sumElems(left).val[0]);
        telemetry.addData("Right raw value", (int) Core.sumElems(right).val[0]);
        telemetry.addData("Left percentage", Math.round(leftValue * 100) + "%");
        telemetry.addData("Right percentage", Math.round(rightValue * 100) + "%");

        boolean rataStanga = leftValue > PERCENT_COLOR_THRESHOLD;
        boolean rataDreapta = rightValue > PERCENT_COLOR_THRESHOLD;

        if (!rataStanga && !rataDreapta) {
            location = Location.UNU;
            telemetry.addData("Nivel rata", "UNU");
        }
        else if (rataDreapta) {
            location = Location.TREI;
            telemetry.addData("Nivel rata", "TREI");
        }
        else{
            location = Location.DOI;
            telemetry.addData("Nivel rata", "DOI");
        }
        telemetry.update();

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

        Scalar colorStone = new Scalar(255, 0, 0);
        Scalar colorSkystone = new Scalar(0, 255, 0);

        Imgproc.rectangle(mat, LEFT_ROI, location == Location.DOI? colorSkystone:colorStone);
        Imgproc.rectangle(mat, RIGHT_ROI, location == Location.TREI? colorSkystone:colorStone);

        return mat;
    }

    public Location getLocation() {
        return location;
    }

}

