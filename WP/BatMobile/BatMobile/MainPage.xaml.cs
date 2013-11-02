using System.Collections.Generic;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace BatMobile
{
    public partial class MainPage
    {
        public MainPage()
        {
            InitializeComponent();
            Touch.FrameReported += Touch_FrameReported;
        }

        private const int mainPoint = 0;
        private double _preX;
        private double _preY;
        private readonly Queue<Point> _queue = new Queue<Point>();
        private readonly DriveManager _driveManager = new DriveManager();

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            _driveManager.Initialize();
        }

        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            _driveManager.Terminate();
        }

        void Touch_FrameReported(object sender, TouchFrameEventArgs e)
        {
            TouchPointCollection pointCollection = e.GetTouchPoints(drawCanvas);
            var position = pointCollection[mainPoint].Position;

            if (pointCollection[mainPoint].Action == TouchAction.Down)
            {
                _queue.Clear();
                _queue.Enqueue(position);
                drawCanvas.Children.Clear();
                _preX = position.X;
                _preY = position.Y;
            }

            if (pointCollection[mainPoint].Action == TouchAction.Up)
            {
                _driveManager.Drive(_queue);
            }
            
            if (pointCollection[mainPoint].Action != TouchAction.Move) return;

            var line = new Line
            {
                X1 = _preX,
                Y1 = _preY,
                X2 = position.X,
                Y2 = position.Y,
                Stroke = new SolidColorBrush(Colors.Black),
                Fill = new SolidColorBrush(Colors.Black)
            };

            drawCanvas.Children.Add(line);

            _preX = position.X;
            _preY = position.Y;
            _queue.Enqueue(position);
        }
    }
}