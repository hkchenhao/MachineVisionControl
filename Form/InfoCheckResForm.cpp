#include "InfoCheckResForm.h"
#include "ui_InfoCheckResForm.h"

InfoCheckResForm::InfoCheckResForm(QWidget *parent) : QWidget(parent), ui(new Ui::InfoCheckResWidget)
{
    ui->setupUi(this);
    this->move((parent->width() - this->width()) / 2, (parent->width() - this->width()) / 2 + 100);
}

InfoCheckResForm::~InfoCheckResForm()
{
    delete ui;
}

void InfoCheckResForm::on_pushButton_clicked()
{
    this->setVisible(false);
}

// widget拖动
void InfoCheckResForm::mousePressEvent(QMouseEvent *event)
{
    ismoveing = true;
    moveposition = event->globalPos() - pos();
    return QWidget::mousePressEvent(event);
}
void InfoCheckResForm::mouseMoveEvent(QMouseEvent *event)
{
    if(ismoveing && (event->buttons() && Qt::LeftButton) &&
      (event->globalPos()-moveposition).manhattanLength() > QApplication::startDragDistance())
    {
        move(event->globalPos()-moveposition);
        moveposition = event->globalPos() - pos();
    }
    return QWidget::mouseMoveEvent(event);
}

void InfoCheckResForm::mouseReleaseEvent(QMouseEvent *event)
{
    ismoveing = false;
}

void InfoCheckResForm::Update(qint32 buf[])
{
    ui->zp1->setText(QString::number(buf[0]));
    ui->cp1->setText(QString::number(buf[1]));
    if((buf[0]+buf[1]) != 0)
        ui->hgl1->setText(QString::number(buf[0] * 100/(buf[0]+buf[1]), 'f', 1) + "%");
    ui->zp2->setText(QString::number(buf[2]));
    ui->cp2->setText(QString::number(buf[3]));
    if((buf[2]+buf[3]) != 0)
        ui->hgl2->setText(QString::number(buf[2]/(buf[2]+buf[3]), 'f', 1) + "%");
    ui->zp3->setText(QString::number(buf[4]));
    ui->cp3->setText(QString::number(buf[5]));
    if((buf[4]+buf[5]) != 0)
        ui->hgl3->setText(QString::number(buf[4] * 100 /(buf[4]+buf[5]), 'f', 1) + "%");

    ui->zzp->setText(QString::number(buf[4]));
    ui->zcp->setText(QString::number(buf[1]+buf[3]+buf[5]));
    if((buf[1]+buf[3]+buf[5]) != 0)
        ui->zhgl->setText(QString::number(buf[4] * 100/(buf[1]+buf[3]+buf[5]), 'f', 1) + "%");
}
