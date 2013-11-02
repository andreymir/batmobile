using System;
using System.Windows;

namespace BatMobile
{
    public static class PointFunctions
    {
        public static double DistanceTo(this Point point1, Point point2)
        {
            var a = point2.X - point1.X;
            var b = point2.Y - point1.Y;

            return Math.Sqrt(a * a + b * b);
        }
    }
}