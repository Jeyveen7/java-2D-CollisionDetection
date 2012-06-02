/* ----------------------------------------------------------------------------
 * Copyright : (c) Svein Inge <Thhethssmuz> Albrigtsen 2012
 * License   : MIT
 * ----------------------------------------------------------------------------
 * 
 * A simple 2D Collision Detection algorithm for a ball/circle and a list of
 * lines.
 * 
 */

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

import java.util.ArrayList;

public class CollisionDetection {



    /* Intersect functions returns point of intersection and a normal vector.
     * for line-line-intersection the normal is the normal of line2.
     * for circle-line-intersection the normal is just the line from centre
     * to the intersection point.
     * Non of the normals are normalized.
     *
     * The rounding is to counteract floating point errors.
     */



    // Line-Line-Intersection -------------------------------------------------
    private static ArrayList<double[]> llIntersection(
            double x1, double y1, double x2, double y2,
            double x3, double y3, double x4, double y4) {

        double d = (y3-y4)*(x2-x1)-(y1-y2)*(x4-x3);
        ArrayList<double[]> p = new ArrayList<double[]>();

        if (d == 0) return p;

        double x = round2(((x4-x3)*(x1*y2-y1*x2)-(x2-x1)*(x3*y4-y3*x4))/d,8);
        double y = round2(((y1-y2)*(x3*y4-y3*x4)-(y3-y4)*(x1*y2-y1*x2))/d,8);

        if (x > round2(min(max(x1,x2),max(x3,x4)),8)) return p;
        if (x < round2(max(min(x1,x2),min(x3,x4)),8)) return p;
        if (y > round2(min(max(y1,y2),max(y3,y4)),8)) return p;
        if (y < round2(max(min(y1,y2),min(y3,y4)),8)) return p;

        p.add(new double[] {x, y, y4-y3, x3-x4});
        return p;
    }


    // Circle-Line-Intersection -----------------------------------------------
    private static ArrayList<double[]> clIntersection(
            double cx, double cy, double r,
            double x1, double y1, double x2, double y2) {

        double dx  = x2-x1;
        double dy  = y2-y1;
        double dl2 = pow(dx,2)+pow(dy,2);
        double det = (x1-cx)*(y2-cy)-(y1-cy)*(x2-cx);
        double dis = pow(r,2)*dl2-pow(det,2);

        ArrayList<double[]> p = new ArrayList<double[]>();

        if (dis <  0) return p;

        else if (dis == 0) {
            double x = round2((det*dy)/dl2,8);
            double y = round2((-det*dx)/dl2,8);
            p.add(new double[] {x+cx, y+cy, x, y});
        }

        else if (dis >  0) {
            double rootdis = sqrt(dis);
            double x_1 = round2((det*dy+signum(dy)*dx*rootdis)/dl2,8);
            double y_1 = round2((-det*dx+abs(dy)*rootdis)/dl2,8);
            double x_2 = round2((det*dy-signum(dy)*dx*rootdis)/dl2,8);
            double y_2 = round2((-det*dx-abs(dy)*rootdis)/dl2,8);
            p.add(new double[] {x_1+cx, y_1+cy, x_1, y_1});
            p.add(new double[] {x_2+cx, y_2+cy, x_2, y_2});
        }

        for (int i = p.size()-1; i >= 0; i--) {
            double[] d = p.get(i);
            if (d[0] < min(x1,x2) || d[0] > max(x1,x2) || 
                d[1] < min(y1,y2) || d[1] > max(y1,y2) ) {
                p.remove(i);
            }
        }
        return p;
    }


    // Custom signum function -------------------------------------------------
    private static int signum(double n) {
        if (n < 0) return -1;
        else       return  1;
    }


    /* Collision Detection ----------------------------------------------------
     *
     * Collision Detection for a circle and a list of lines.
     * Don't actually check for collisions of the circle with any lines, but
     * draws a circle at each endpoint of each line, and two lines tangent to 
     * both circles of the line and parallel to the original line (and with the
     * same length), for each line. And checks if the movement vector of the 
     * circle centre has crossed any of these lines or circles.
     *
     */

    // call AFTER velocity and position update
    public static double[] collisionDetection(
            double cx, double cy, double r,
            double vx, double vy,
            ArrayList<int[]> world) {

        ArrayList<double[]> collisions = new ArrayList<double[]>();

        // start position of ball centre or vv start
        double cx0 = cx-vx;
        double cy0 = cy-vy;

        // end position of ball centre or vv end
        double cx1 = cx;
        double cy1 = cy;

        // current velocity
        double nvx = vx;
        double nvy = vy;

        while (true) {

            ArrayList<double[]> intersects = new ArrayList<double[]>();

            // Construct list of all intersections for the current velocity
            for (int[] w : world) {
                intersects.addAll(clIntersection(w[0], w[1], r, cx0, cy0, cx1, cy1));

                if (w[0] != w[2] || w[1] != w[3]) {
                    intersects.addAll(clIntersection(w[2], w[3], r, cx0, cy0, cx1, cy1));

                    double theta = (w[2]-w[0] == 0) ? PI/2 : atan(((double)w[3]-w[1])/(w[2]-w[0]));
                    double rsin  = r*sin(theta);
                    double rcos  = r*cos(theta);
                    intersects.addAll(llIntersection(cx0, cy0, cx1, cy1,
                        w[0]-rsin, w[1]+rcos, w[2]-rsin, w[3]+rcos));
                    intersects.addAll(llIntersection(cx0, cy0, cx1, cy1,
                        w[0]+rsin, w[1]-rcos, w[2]+rsin, w[3]-rcos));
                }
            }

            double vmag = pow(cx1-cx0,2)+pow(cy1-cy0,2);
            double[] collision = null;

            // find the first intersect and save it two the "collision"
            for (double[] d : intersects) {
                double mag = pow(d[0]-cx0,2)+pow(d[1]-cy0,2);

                if (mag == vmag && collision != null) {
                    // if two intersections happen simultaneously
                    // normalize and merge normal vectors
                    double l1 = sqrt(pow(collision[2],2)+pow(collision[3],2));
                    double l2 = sqrt(pow(d[2],2)+pow(d[3],2));
                    collision[2] = (collision[2]/l1+d[2]/l2);
                    collision[3] = (collision[3]/l1+d[3]/l2);
                }

                else if (mag < vmag && !elementIn(collisions, d)) {
                    vmag = mag;
                    collision = d;
                }
            }

            // if a collision is found; add it to the collisions array.
            // if so run while loop again with new velocity vector to find any
            // collisions for the new trajectory
            if (collision != null) {
                collisions.add(collision);
                double[] ref = reflectVector( 
                    (cx1-cx0)-(collision[0]-cx0),
                    (cy1-cy0)-(collision[1]-cy0), 
                    collision[2], collision[3]);
                cx0 = collision[0];
                cy0 = collision[1];
                cx1 = cx0+ref[0];
                cy1 = cy0+ref[1];
                double[] vel = reflectVector(nvx, nvy, 
                    collision[2], collision[3]);
                nvx = vel[0];
                nvy = vel[1];
            }

            else break;
        }

        return new double[] {cx1, cy1, nvx, nvy};
    }


    // Vector reflection function ---------------------------------------------
    private static double[] reflectVector(double vx, double vy,
                                         double nx, double ny) {
        double nmag = pow(nx,2)+pow(ny,2);
        return new double[] {
                vx-((2*nx*nx*vx)/nmag+(2*nx*ny*vy)/nmag),
                vy-((2*ny*nx*vx)/nmag+(2*ny*ny*vy)/nmag)
            };
    }


    // Custom "contains" function ---------------------------------------------
    // only evaluates the first two array items, as this should suffice in this
    // context.
    private static boolean elementIn(ArrayList<double[]> list, double[] elem) {
        for (double[] d : list) {
            if ( round2(d[0],5) == round2(elem[0],5) &&
                 round2(d[1],5) == round2(elem[1],5) ) return true;
        }
        return false;
    }


    // Round to decimal place -------------------------------------------------
    private static double round2(double num, int i) {
        double d = pow(10,i);
        return Math.round(num*d)/d;
    }


    // toClose ----------------------------------------------------------------
    // parts of old Circle-Line-Intersection function 
    public static boolean toClose(
            double c1, double c2, double r,
            int x1, int y1, int x2, int y2) {

        if (x1 == x2 && y1 == y2) {
            if ((x1-c1)*(x1-c1)+(y1-c2)*(y1-c2) > r*r) return false;
            else return true;
        }

        double dx  = x2-x1;
        double dy  = y2-y1;
        double dr2 = dx*dx+dy*dy;

        // find intersection between the line and its normal through the 
        // circle centre, this point will also be the closest point on the line
        // to the circle centre.
        double tn  = ((c1-x1)*dx+(c2-y1)*dy)/dr2;
        double xn  = x1+tn*dx;
        double yn  = y1+tn*dy;

        // find the length of the line from (xn,yn) to (c1, c2)
        double dn2 = (xn-c1)*(xn-c1)+(yn-c2)*(yn-c2);

        // if length of the normal intersect to the circle centre is grater 
        // than the radius -> then there is no intersection between the circle
        // and the line.
        if (dn2 > r*r) return false;
 
        // test if the intersection point with the infinite line is out side 
        // the bounds of the line segment
        if ( (xn > Math.max(x1, x2)+r) || 
             (xn < Math.min(x1, x2)-r) ||
             (yn > Math.max(y1, y2)+r) ||
             (yn < Math.min(y1, y2)-r) ) return false;

        // edge cases
        if ( (xn > Math.max(x1, x2)) ||
             (xn < Math.min(x1, x2)) ||
             (yn > Math.max(y1, y2)) ||
             (yn < Math.min(y1, y2)) ) {
            double db1 = (x1-c1)*(x1-c1)+(y1-c2)*(y1-c2);
            double db2 = (x2-c1)*(x2-c1)+(y2-c2)*(y2-c2);

            // last non-collision scenario
            if (db1 > r*r && db2 > r*r) return false;
        }

        return true;
    }

}
