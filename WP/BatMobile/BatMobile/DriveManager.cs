using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Windows;
using Windows.Networking.Proximity;
using BluetoothConnectionManager;

namespace BatMobile
{
    public class DriveManager
    {
        private const byte maxSpeed = 126;
        private const byte stopSpeed = 0;
        private const string command = "d";
        private readonly ConnectionManager _connectionManager = new ConnectionManager();

        public void Initialize()
        {
            _connectionManager.Initialize();
            AppToDevice();
        }

        public void Drive(Queue<Point> queue)
        {
            Start();
            if (queue.Count < 4) return;

            var prevPoint = queue.Dequeue();
            var currentPoint = GetNextPoint(queue, prevPoint).Item1;
            var nextTuple = GetNextPoint(queue, currentPoint);

            var nextPoit = nextTuple.Item1;
            var delay = nextTuple.Item2;


            while (queue.Count > 0)
            {

                var ab = prevPoint.DistanceTo(currentPoint);
                var bc = currentPoint.DistanceTo(nextPoit);
                var ca = nextPoit.DistanceTo(prevPoint);

                var angel = Angle(ab, bc, ca);
                var isLeft = IsLeft(prevPoint, currentPoint, nextPoit);

                Process(angel, isLeft, delay);

                prevPoint = currentPoint;
                currentPoint = nextPoit;
                nextTuple = GetNextPoint(queue, nextPoit);
                nextPoit = nextTuple.Item1;
                delay = nextTuple.Item2;
            } 


            Stop();
        }

        private static Tuple<Point, int> GetNextPoint(Queue<Point> queue, Point prevPoint)
        {
            var nextPoit = queue.Dequeue();
            var i = 1;
            while (queue.Count > 0 && Math.Abs(prevPoint.X - nextPoit.X) < 10 && Math.Abs(prevPoint.Y - nextPoit.Y) < 10)
            {
                nextPoit = queue.Dequeue();
                i++;
            }
            return new Tuple<Point, int>(nextPoit, i);
        }

        private static bool IsLeft(Point prevPoint, Point currentPoint, Point nextPoit)
        {
            var a = currentPoint.Y - prevPoint.Y;
            var b = prevPoint.X - currentPoint.X;
            var c = currentPoint.X*prevPoint.Y - currentPoint.Y*prevPoint.X;

            return a * nextPoit.X + b * nextPoit.Y + c > 0;
        }

        private static double Angle(double ab, double bc, double ac)
        {
            return Math.Acos((ab * ab + bc * bc - ac * ac) / (2 * bc * ab)) * 180 / Math.PI;
        }

        private void Process(double angel, bool isLeft, int delay)
        {
            if (angel > 175) // forward
            {
                _connectionManager.SendCommand(command, maxSpeed, maxSpeed);
                Thread.Sleep(100 * delay);
            }
            else if (angel > 150) // forward
            {
                if (isLeft)
                {
                    _connectionManager.SendCommand(command, (byte)GetSpeed(angel), maxSpeed);
                }
                else
                {
                    _connectionManager.SendCommand(command, maxSpeed, (byte)GetSpeed(angel));
                }

                Thread.Sleep(100 * delay);
            }
            else if (angel >= 90) // slow
            {
                if (isLeft)
                {
                    _connectionManager.SendCommand(command, (byte)(GetSpeed(angel) * 0.3), maxSpeed);
                }
                else
                {
                    _connectionManager.SendCommand(command, maxSpeed, (byte)(GetSpeed(angel) * 0.3));
                }

                Thread.Sleep(300);
            }
            else if (angel > 0) // fast
            {
                if (isLeft)
                {
                    _connectionManager.SendCommand(command, (byte)GetSpeed(angel), maxSpeed);
                }
                else
                {
                    _connectionManager.SendCommand(command, maxSpeed, (byte)GetSpeed(angel));
                }
                Thread.Sleep(900);
            }
        }

        private static int GetSpeed(double angel)
        {
            return (int) (maxSpeed*(angel - 90)/90);
        }

        private void Start()
        {
            _connectionManager.SendCommand("mm");
            _connectionManager.SendCommand(command, maxSpeed, maxSpeed);
        }

        private void Stop()
        {
            _connectionManager.SendCommand(command, stopSpeed, stopSpeed);
            _connectionManager.SendCommand("mi");
        }

        private async void AppToDevice()
        {
            try
            {
                PeerFinder.AlternateIdentities["Bluetooth:Paired"] = "";
                var pairedDevices = await PeerFinder.FindAllPeersAsync();

                if (pairedDevices.Count == 0)
                {
                    Debug.WriteLine("No paired devices were found.");
                }
                else
                {
                    //Debug.WriteLine(pairedDevices[0].HostName);
                    _connectionManager.Connect(pairedDevices[0].HostName);
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());
            }
        }

        public void Terminate()
        {
            _connectionManager.Terminate();
        }
    }
}