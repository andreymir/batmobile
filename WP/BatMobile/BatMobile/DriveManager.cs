﻿using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq.Expressions;
using System.Threading;
using System.Windows;
using Windows.Networking.Proximity;
using Windows.Phone.Media.Capture;
using Windows.System;
using BluetoothConnectionManager;

namespace BatMobile
{
    public class DriveManager
    {
        private const byte maxSpeed = 126;
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
                var isRight = IsRight(prevPoint, currentPoint, nextPoit);

                Process(angel, isRight, delay);

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
            while (queue.Count > 0 && Math.Abs(prevPoint.X - nextPoit.X) < 10 && Math.Abs(prevPoint.Y - nextPoit.Y) < 10)
            {
                nextPoit = queue.Dequeue();
            }
            return nextPoit;
        }

        private static bool IsRight(Point prevPoint, Point currentPoint, Point nextPoit)
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

        private void Process(double angel, bool isRight, int delay)
        {
            if (angel > 170) //forvard
            {
                _connectionManager.SendCommand(command, maxSpeed, maxSpeed);
            }
            else if (angel > 90) // 
            {
                if (!isRight)
                {
                    _connectionManager.SendCommand(command, maxSpeed, 30);
                }
                else
                {
                    _connectionManager.SendCommand(command, 30, maxSpeed);
                }

                Thread.Sleep(100);
            }
            else if (angel > 0) // fast
            {
                if (!isRight)
                {
                    _connectionManager.SendCommand(command, maxSpeed, 250);
                }
                else
                {
                    _connectionManager.SendCommand(command, 250, maxSpeed);
                }

                Thread.Sleep(100);
            }

            Thread.Sleep(10 * delay);
            //else // back
            //{
            //    _connectionManager.SendCommand(command, 140, 140);
            //}
        }

        private void Start()
        {
            const byte left = 100;
            const byte right = 100;
            _connectionManager.SendCommand(command, left, right);
        }

        private void Stop()
        {
            const byte left = 0;
            const byte right = 0;
            _connectionManager.SendCommand(command, left, right);
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