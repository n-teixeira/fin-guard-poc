import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get cash flow', () => {
    const mockCashFlow = { userId: 'u1', balance: 100, totalCredits: 200, totalDebits: 100 };
    service.getCashFlow().subscribe((cf) => {
      expect(cf.balance).toBe(100);
    });
    const req = httpMock.expectOne((r) => r.url.includes('/cash-flow'));
    expect(req.request.method).toBe('GET');
    req.flush(mockCashFlow);
  });

  it('should process notification', () => {
    const content = 'Pix recebido de João. Valor R$ 100,00';
    const packageName = 'com.nubank';
    const mockTransaction = { id: '1', amount: 100, type: 'PIX_IN', origin: 'João' };

    service.processNotification(content, packageName).subscribe((t) => {
      expect(t.amount).toBe(100);
      expect(t.type).toBe('PIX_IN');
    });
    const req = httpMock.expectOne((r) => r.url.includes('/notifications/process'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ content, packageName });
    req.flush(mockTransaction);
  });

  it('should get transactions', () => {
    const mockTransactions = [
      { id: '1', amount: 100, type: 'PIX_IN', origin: 'João' },
      { id: '2', amount: 50, type: 'PAYMENT', origin: 'Loja' },
    ];

    service.getTransactions().subscribe((list) => {
      expect(Array.isArray(list)).toBe(true);
      expect(list.length).toBe(2);
      expect(list[0].amount).toBe(100);
    });
    const req = httpMock.expectOne((r) => r.url.includes('/transactions'));
    expect(req.request.method).toBe('GET');
    req.flush(mockTransactions);
  });

  it('should return empty array when getTransactions returns non-array', () => {
    service.getTransactions().subscribe((list) => {
      expect(Array.isArray(list)).toBe(true);
      expect(list.length).toBe(0);
    });
    const req = httpMock.expectOne((r) => r.url.includes('/transactions'));
    req.flush(null);
  });
});
