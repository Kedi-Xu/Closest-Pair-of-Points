import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.util.IdentityHashMap;

public class ClosestPair {
    public static void main(String[] args){

        String mode = "RANDOM";

        Point[] points;

        if (mode.equals("FIXED")) {

            points = new Point[]{
                    new Point(1, 1),
                    new Point(2, 2),
                    new Point(2, 2),
                    new Point(5, 5)
            };
        } else if (mode.equals("RANDOM")) {
            int numberOfPoints = 500;
            points = new Point[numberOfPoints];
            Random random = new Random(42);

            for (int i = 0; i < numberOfPoints; i++) {

                double randomX = random.nextDouble() * 100;
                double randomY = random.nextDouble() * 100;

                points[i] = new Point(randomX, randomY);
            }
        } else {
            System.out.println("Invalid mode");
            return;
        }

        if (points.length < 2) {
            System.out.println("Invalid input");
            return;
        }

        Point[] pointsByX = points.clone();

//      Sort by x-coordinate
        Arrays.sort(pointsByX, (Point p1, Point p2) ->
                Double.compare(p1.x, p2.x));

        Point[] pointsByY = points.clone();

//      Sort by y-coordinate
        Arrays.sort(pointsByY, (Point p1, Point p2) ->
                Double.compare(p1.y, p2.y));

        ClosestPairResult bruteForceResult =
                findClosestPairBruteForceResult(points);

        double bruteForceDistance = bruteForceResult.distance;

        System.out.println("\nBrute Force Closest Pair:");

        System.out.println(
                "(" + bruteForceResult.firstPoint.x + ", "
                        + bruteForceResult.firstPoint.y + ")");

        System.out.println(
                "(" + bruteForceResult.secondPoint.x + ", "
                        + bruteForceResult.secondPoint.y + ")");

        System.out.println(
                "Minimum Distance: " + bruteForceResult.distance);

//      Version 1.0 Result
        ClosestPairResult recursiveResult =
                findClosestPairRecursiveResult(
                        pointsByX, 0, pointsByX.length - 1);

        double recursiveDistance = recursiveResult.distance;

        // Version 2.0 Result
        ClosestPairResult optimizedResult =
                findClosestPairOptimizedResult(pointsByX, pointsByY);

        double optimizedDistance = optimizedResult.distance;

        System.out.println("Number of Points: " + points.length);

        System.out.println("\nDistance Comparison");
        System.out.println("Brute Force Distance: " + bruteForceDistance);
        System.out.println("Version 1.0 Distance: " + recursiveDistance);
        System.out.println("Version 2.0 Distance: " + optimizedDistance);

        System.out.println("Version 1.0 Match: " +
                        (Math.abs(bruteForceDistance - recursiveDistance) < 1e-9)
        );

        System.out.println("Version 2.0 Match: " +
                        (Math.abs(bruteForceDistance - optimizedDistance) < 1e-9));

        benchmark(points);

    }

    public static ClosestPairResult bruteForceRangeResult(
            Point[] points, int left, int right) {

        double minDistance = Double.MAX_VALUE;

        Point firstPoint = null;
        Point secondPoint = null;

        for (int i = left; i <= right; i++) {

            for (int j = i + 1; j <= right; j++) {

                double distance = calculateDistance(
                        points[i], points[j]);

                if (distance < minDistance) {

                    minDistance = distance;

                    firstPoint = points[i];
                    secondPoint = points[j];
                }
            }
        }

        return new ClosestPairResult(
                firstPoint, secondPoint, minDistance);
    }

    public static ClosestPairResult findClosestPairBruteForceResult(
            Point[] points) {

        double minDistance = Double.MAX_VALUE;

        Point firstPoint = null;
        Point secondPoint = null;

        for (int i = 0; i < points.length; i++) {

            for (int j = i + 1; j < points.length; j++) {

                double distance = calculateDistance(
                        points[i], points[j]);

                if (distance < minDistance) {

                    minDistance = distance;

                    firstPoint = points[i];
                    secondPoint = points[j];
                }
            }
        }

        return new ClosestPairResult(
                firstPoint, secondPoint, minDistance);
    }

    public static ClosestPairResult findClosestPairRecursiveResult(
            Point[] points, int left, int right) {

        // Base Case
        if (right - left + 1 <= 3) {
            return bruteForceRangeResult(points, left, right);
        }

        int mid = (left + right) / 2;

        // Solve left and right halves
        ClosestPairResult leftResult =
                findClosestPairRecursiveResult(points, left, mid);

        ClosestPairResult rightResult =
                findClosestPairRecursiveResult(points, mid + 1, right);

        ClosestPairResult bestResult;

        if (leftResult.distance < rightResult.distance) {
            bestResult = leftResult;
        } else {
            bestResult = rightResult;
        }

        double dividingX = points[mid].x;

        ArrayList<Point> strip = new ArrayList<>();

        for (int i = left; i <= right; i++) {

            if (Math.abs(points[i].x - dividingX)
                    < bestResult.distance) {

                strip.add(points[i]);
            }
        }

        // Sort strip by y-coordinate
        strip.sort((Point p1, Point p2) ->
                Double.compare(p1.y, p2.y));

        // Compare the next 7 points
        for (int i = 0; i < strip.size(); i++) {

            for (int j = i + 1;
                 j < strip.size() && j <= i + 7; j++) {

                double distance = calculateDistance(strip.get(i), strip.get(j));

                if (distance < bestResult.distance) {

                    bestResult = new ClosestPairResult(
                            strip.get(i),
                            strip.get(j),
                            distance);
                }
            }
        }

        return bestResult;
    }

    public static Point[][] splitByY(Point[] pointsByX, Point[] pointsByY, int leftSize) {

        IdentityHashMap<Point, Integer> leftCounts = new IdentityHashMap<>();

        for (int i = 0; i < leftSize; i++) {

            Point point = pointsByX[i];
            leftCounts.put(point, leftCounts.getOrDefault(point, 0) + 1);
        }

        Point[] leftPointsByY = new Point[leftSize];

        Point[] rightPointsByY = new Point[pointsByY.length - leftSize];

        int leftIndex = 0;
        int rightIndex = 0;

        // Divide pointsByY while preserving Y order.
        for (int i = 0; i < pointsByY.length; i++) {

            Point point = pointsByY[i];

            Integer remaining = leftCounts.get(point);

            if (remaining != null && remaining > 0) {

                leftPointsByY[leftIndex] = point;
                leftIndex++;

                leftCounts.put(point, remaining - 1);

            } else {
                rightPointsByY[rightIndex] = point;
                rightIndex++;
            }
        }
        return new Point[][]{leftPointsByY, rightPointsByY
        };
    }

    public static ClosestPairResult findClosestPairOptimizedResult(
            Point[] pointsByX, Point[] pointsByY) {

        int n = pointsByX.length;

        // Base Case
        if (n <= 3) {
            return bruteForceRangeResult(
                    pointsByX, 0, n - 1);
        }

        int mid = n / 2;

        double dividingX =
                (pointsByX[mid - 1].x + pointsByX[mid].x) / 2.0;

        Point[] leftPointsByX =
                Arrays.copyOfRange(pointsByX, 0, mid);

        Point[] rightPointsByX =
                Arrays.copyOfRange(pointsByX, mid, n);

        Point[][] dividedByY =
                splitByY(pointsByX, pointsByY, mid);

        Point[] leftPointsByY = dividedByY[0];
        Point[] rightPointsByY = dividedByY[1];

        // Recursive calls
        ClosestPairResult leftResult =
                findClosestPairOptimizedResult(
                        leftPointsByX, leftPointsByY);

        ClosestPairResult rightResult =
                findClosestPairOptimizedResult(
                        rightPointsByX, rightPointsByY);

        ClosestPairResult bestResult;

        if (leftResult.distance < rightResult.distance) {
            bestResult = leftResult;
        } else {
            bestResult = rightResult;
        }

        double minDistance = bestResult.distance;

        ArrayList<Point> strip = new ArrayList<>();

        for (int i = 0; i < pointsByY.length; i++) {

            if (Math.abs(pointsByY[i].x - dividingX) < minDistance) {
                strip.add(pointsByY[i]);
            }
        }

        for (int i = 0; i < strip.size(); i++) {

            for (int j = i + 1;
                 j < strip.size() && j <= i + 7;
                 j++) {

                double distance = calculateDistance(
                        strip.get(i), strip.get(j));

                if (distance < minDistance) {

                    minDistance = distance;

                    bestResult = new ClosestPairResult(
                            strip.get(i),
                            strip.get(j),
                            distance);
                }
            }
        }

        return bestResult;
    }

    public static void benchmark(Point[] points) {

        int repetitions = 30;

        System.out.println("\nPerformance Comparison");
        System.out.println("Number of Points: " + points.length);


        System.out.println("Warming up...");

        for (int i = 0; i < 5; i++) {

            // Warm up Brute Force
            findClosestPairBruteForceResult(points);

            // Warm up Version 1.0
            Point[] warmUpX1 = points.clone();

            Arrays.sort(warmUpX1, (p1, p2) ->
                    Double.compare(p1.x, p2.x));

            findClosestPairRecursiveResult(
                    warmUpX1, 0, warmUpX1.length - 1);

            // Warm up Version 2.0
            Point[] warmUpX2 = points.clone();
            Point[] warmUpY2 = points.clone();

            Arrays.sort(warmUpX2, (p1, p2) ->
                    Double.compare(p1.x, p2.x));

            Arrays.sort(warmUpY2, (p1, p2) ->
                    Double.compare(p1.y, p2.y));

            findClosestPairOptimizedResult(
                    warmUpX2, warmUpY2);
        }

        long bruteForceTotal = 0;
        long version1Total = 0;
        long version2Total = 0;

        ClosestPairResult bruteForceResult = null;
        ClosestPairResult version1Result = null;
        ClosestPairResult version2Result = null;


        for (int run = 0; run < repetitions; run++) {

            long startTime = System.nanoTime();

            bruteForceResult =
                    findClosestPairBruteForceResult(points);

            long endTime = System.nanoTime();

            bruteForceTotal += endTime - startTime;


            // V1
            startTime = System.nanoTime();

            Point[] pointsByX1 = points.clone();

            Arrays.sort(pointsByX1, (p1, p2) ->
                    Double.compare(p1.x, p2.x));

            version1Result =
                    findClosestPairRecursiveResult(
                            pointsByX1, 0, pointsByX1.length - 1);

            endTime = System.nanoTime();

            version1Total += endTime - startTime;

            // V2
            startTime = System.nanoTime();

            Point[] pointsByX2 = points.clone();
            Point[] pointsByY2 = points.clone();

            Arrays.sort(pointsByX2, (p1, p2) ->
                    Double.compare(p1.x, p2.x));

            Arrays.sort(pointsByY2, (p1, p2) ->
                    Double.compare(p1.y, p2.y));

            version2Result =
                    findClosestPairOptimizedResult(
                            pointsByX2, pointsByY2);

            endTime = System.nanoTime();

            version2Total += endTime - startTime;
        }

        // Calculate average time
        double bruteForceAverage =
                bruteForceTotal / (double) repetitions / 1_000_000.0;

        double version1Average =
                version1Total / (double) repetitions / 1_000_000.0;

        double version2Average =
                version2Total / (double) repetitions / 1_000_000.0;

        System.out.println("\nAverage Running Time (" +
                repetitions + " runs)");

        System.out.println("Brute Force: " +
                bruteForceAverage + " ms");

        System.out.println("Version 1.0: " +
                version1Average + " ms");

        System.out.println("Version 2.0: " +
                version2Average + " ms");

        System.out.println("\nCorrectness Check:");

        System.out.println("Version 1.0 Match: " +
                (Math.abs(bruteForceResult.distance -
                        version1Result.distance) < 1e-9));

        System.out.println("Version 2.0 Match: " +
                (Math.abs(bruteForceResult.distance -
                        version2Result.distance) < 1e-9));
    }

    public static double calculateDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;

        return Math.sqrt(dx * dx + dy * dy);

    }
}

class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class ClosestPairResult {

    Point firstPoint;
    Point secondPoint;
    double distance;

    public ClosestPairResult(
            Point firstPoint,
            Point secondPoint,
            double distance) {

        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.distance = distance;
    }
}
